package org.codequistify.master.stage.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.stage.domain.vo.QuestionIndex;
import org.codequistify.master.stage.domain.vo.StageId;
import org.junit.jupiter.api.Test;

class PlayerStageSolvingTest {

    @Test
    void Player가_Stage_풀이를_시작하면_IN_PROGRESS와_첫번째_문항을_가진다() {
        PlayerStageSolving solving = PlayerStageSolving.start(new PlayerId("player-id"), new StageId("1"));

        assertThat(solving.playerId().value()).isEqualTo("player-id");
        assertThat(solving.stageId().value()).isEqualTo("1");
        assertThat(solving.status()).isEqualTo(SolvingStatus.IN_PROGRESS);
        assertThat(solving.currentQuestionIndex()).isEqualTo(QuestionIndex.FIRST);
    }

    @Test
    void IN_PROGRESS_상태에서_현재_문항을_진행하고_완료할_수_있다() {
        PlayerStageSolving solving = PlayerStageSolving.start(new PlayerId("player-id"), new StageId("1"));

        solving.progressTo(new QuestionIndex(2)).completeAt(new QuestionIndex(3));

        assertThat(solving.currentQuestionIndex().value()).isEqualTo(3);
        assertThat(solving.status()).isEqualTo(SolvingStatus.COMPLETED);
        assertThat(solving.isCompleted()).isTrue();
    }

    @Test
    void COMPLETED_상태의_풀이는_포기할_수_없다() {
        PlayerStageSolving solving = PlayerStageSolving.start(new PlayerId("player-id"), new StageId("1"))
                .completeAt(new QuestionIndex(1));

        assertThatThrownBy(solving::abandon).isInstanceOf(IllegalStateException.class);
    }
}
