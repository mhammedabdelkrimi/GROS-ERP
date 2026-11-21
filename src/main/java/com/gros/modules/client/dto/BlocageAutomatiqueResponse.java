package com.gros.modules.client.dto;

import java.util.List;

public record BlocageAutomatiqueResponse(int nombre, List<Long> clientIds) {}