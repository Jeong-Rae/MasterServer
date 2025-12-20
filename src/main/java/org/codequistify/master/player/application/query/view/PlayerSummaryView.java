package org.codequistify.master.player.application.query.view;

import java.util.List;
import java.util.UUID;

public record PlayerSummaryView(
    UUID playerId, String nickname, long point, List<String> roles, List<String> permissions) {}
