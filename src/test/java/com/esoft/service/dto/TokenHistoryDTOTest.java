package com.esoft.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.esoft.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TokenHistoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TokenHistoryDTO.class);
        TokenHistoryDTO tokenHistoryDTO1 = new TokenHistoryDTO();
        tokenHistoryDTO1.setId(1L);
        TokenHistoryDTO tokenHistoryDTO2 = new TokenHistoryDTO();
        assertThat(tokenHistoryDTO1).isNotEqualTo(tokenHistoryDTO2);
        tokenHistoryDTO2.setId(tokenHistoryDTO1.getId());
        assertThat(tokenHistoryDTO1).isEqualTo(tokenHistoryDTO2);
        tokenHistoryDTO2.setId(2L);
        assertThat(tokenHistoryDTO1).isNotEqualTo(tokenHistoryDTO2);
        tokenHistoryDTO1.setId(null);
        assertThat(tokenHistoryDTO1).isNotEqualTo(tokenHistoryDTO2);
    }
}
