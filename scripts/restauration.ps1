param([Parameter(Mandatory = $true)][string]$Fichier)
$ErrorActionPreference = "Stop"
Set-Location (Split-Path $PSScriptRoot -Parent)

Get-Content .env | ForEach-Object {
    if ($_ -match '^\s*([A-Za-z_][A-Za-z0-9_]*)\s*=\s*(.*?)\s*$') {
        Set-Item -Path "env:$($Matches[1])" -Value $Matches[2]
    }
}

$nom = Split-Path $Fichier -Leaf
$connexion = "system/$($env:ORACLE_ADMIN_PASSWORD)@localhost:1521/FREEPDB1"
$schema = "schemas=$($env:DB_USER)"
$fichierDump = "dumpfile=$nom"
$fichierLog = "logfile=restauration-$nom.log"

docker exec gros-oracle mkdir -p /tmp/gros-backup
docker cp $Fichier "gros-oracle:/tmp/gros-backup/$nom"
docker exec -u root gros-oracle chmod -R a+rwX /tmp/gros-backup

"CREATE OR REPLACE DIRECTORY GROS_BACKUP AS '/tmp/gros-backup';`nEXIT;`n" |
    docker exec -i gros-oracle sqlplus -s $connexion
if ($LASTEXITCODE -ne 0) { throw "Création du répertoire Oracle impossible" }

docker exec gros-oracle impdp $connexion $schema directory=GROS_BACKUP $fichierDump $fichierLog
if ($LASTEXITCODE -ne 0) { throw "impdp a échoué" }
Write-Host "Restauration terminée. Démarre l'API : docker compose up -d"