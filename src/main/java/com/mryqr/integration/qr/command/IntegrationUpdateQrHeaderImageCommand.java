package com.mryqr.integration.qr.command;

import com.mryqr.common.utils.Command;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import static com.mryqr.common.utils.MryConstants.MAX_URL_LENGTH;
import static lombok.AccessLevel.PRIVATE;

@Value
@Builder
@AllArgsConstructor(access = PRIVATE)
public class IntegrationUpdateQrHeaderImageCommand implements Command {

    @Size(max = MAX_URL_LENGTH)
    private final String headerImageUrl;

}
