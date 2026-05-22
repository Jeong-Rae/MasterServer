package org.codequistify.master.stage.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class StageImageTypeTest {

    @Test
    void StageImageType은_API_code와_resource_code를_제공한다() {
        StageImageType stageImage = StageImageType.S1001;

        assertThat(stageImage.code()).isEqualTo("S1001");
        assertThat(stageImage.resourceCode()).isEqualTo("s1001");
    }

    @Test
    void resource_code로_StageImageType을_복원한다() {
        assertThat(StageImageType.fromResourceCode("s1001")).isEqualTo(StageImageType.S1001);
        assertThat(StageImageType.fromResourceCode("S1001")).isEqualTo(StageImageType.S1001);
    }

    @Test
    void 빈_resource_code는_예외를_던진다() {
        assertThatThrownBy(() -> StageImageType.fromResourceCode(" ")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 검색어가_StageImage_resource_code에_포함되면_true를_반환한다() {
        assertThat(StageImageType.S1001.contains("S10")).isTrue();
        assertThat(StageImageType.S1001.contains("x99")).isFalse();
        assertThat(StageImageType.S1001.contains(" ")).isFalse();
    }
}
