package com.esoft.domain;

import static com.esoft.domain.AccessTokenTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.esoft.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AccessTokenTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AccessToken.class);
        AccessToken accessToken1 = getAccessTokenSample1();
        AccessToken accessToken2 = new AccessToken();
        assertThat(accessToken1).isNotEqualTo(accessToken2);

        accessToken2.setId(accessToken1.getId());
        assertThat(accessToken1).isEqualTo(accessToken2);

        accessToken2 = getAccessTokenSample2();
        assertThat(accessToken1).isNotEqualTo(accessToken2);
    }
}
