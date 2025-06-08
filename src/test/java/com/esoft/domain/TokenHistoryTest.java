package com.esoft.domain;

import static com.esoft.domain.TokenHistoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.esoft.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TokenHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TokenHistory.class);
        TokenHistory tokenHistory1 = getTokenHistorySample1();
        TokenHistory tokenHistory2 = new TokenHistory();
        assertThat(tokenHistory1).isNotEqualTo(tokenHistory2);

        tokenHistory2.setId(tokenHistory1.getId());
        assertThat(tokenHistory1).isEqualTo(tokenHistory2);

        tokenHistory2 = getTokenHistorySample2();
        assertThat(tokenHistory1).isNotEqualTo(tokenHistory2);
    }
}
