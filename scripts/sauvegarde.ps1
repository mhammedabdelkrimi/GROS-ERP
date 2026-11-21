$ErrorActionPreference = "Stop"
Set-Location (Split-Path $PSScriptRoot -Parent)

Get-Content .env | ForEach-Object {
    if ($_ -match '^\s*([A-Za-z_][A-Za-z0-9_]*)\s*=\s*(.*?)\s*$') {
        Set-Item -Path "env:$($Matches[1])" -Value $Matches[2]
    }
}

$stamp = Get-Date -Format "yyyyMMdd-HHmmss"
$dump = "gros-$stamp.dmp"
$log = "gros-$stamp.log"
$connexion = "system/$($env:ORACLE_ADMIN_PASSWORD)@localhost:1521/FREEPDB1"
$schema = "schemas=$($env:DB_USER)"
$fichierDump = "dumpfile=$dump"
$fichierLog = "logfile=$log"

docker exec gros-oracle mkdir -p /tmp/gros-backup
if ($LASTEXITCODE -ne 0) { throw "Conteneur gros-oracle introuvable ou arrêté" }

"CREATE OR REPLACE DIRECTORY GROS_BACKUP AS '/tmp/gros-backup';`nEXIT;`n" |
    docker exec -i gros-oracle sqlplus -s $connexion
if ($LASTEXITCODE -ne 0) { throw "Création du répertoire Oracle impossible" }

docker exec gros-oracle expdp $connexion $schema directory=GROS_BACKUP $fichierDump $fichierLog
if ($LASTEXITCODE -ne 0) { throw "expdp a échoué : voir backups\$log" }

New-Item -ItemType Directory -Force backups | Out-Null
docker cp "gros-oracle:/tmp/gros-backup/$dump" "backups/$dump"
docker cp "gros-oracle:/tmp/gros-backup/$log" "backups/$log"
docker exec gros-oracle rm -f "/tmp/gros-backup/$dump" "/tmp/gros-backup/$log"

# Conservation : 14 jours
Get-ChildItem backups -Filter "gros-*" | Where-Object { $_.LastWriteTime -lt (Get-Date).AddDays(-14) } | Remove-Item
Write-Host "Sauvegarde créée : backups\$dump"