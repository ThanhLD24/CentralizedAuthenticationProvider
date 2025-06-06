package com.esoft.domain;

import static com.esoft.domain.ApplicationSystemTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.esoft.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ApplicationSystemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ApplicationSystem.class);
        ApplicationSystem applicationSystem1 = getApplicationSystemSample1();
        ApplicationSystem applicationSystem2 = new ApplicationSystem();
        assertThat(applicationSystem1).isNotEqualTo(applicationSystem2);

        applicationSystem2.setId(applicationSystem1.getId());
        assertThat(applicationSystem1).isEqualTo(applicationSystem2);

        applicationSystem2 = getApplicationSystemSample2();
        assertThat(applicationSystem1).isNotEqualTo(applicationSystem2);
    }
}
