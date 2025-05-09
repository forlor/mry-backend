package com.mryqr.core.app.command;

import com.mryqr.common.utils.Command;
import com.mryqr.common.validation.collection.NoBlankString;
import com.mryqr.common.validation.collection.NoDuplicatedString;
import com.mryqr.common.validation.id.member.MemberId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.List;

import static com.mryqr.common.utils.MryConstants.MAX_APP_MANAGER_SIZE;
import static lombok.AccessLevel.PRIVATE;

@Value
@Builder
@AllArgsConstructor(access = PRIVATE)
public class SetAppManagersCommand implements Command {
    @Valid
    @NotNull
    @NoBlankString
    @NoDuplicatedString
    @Size(max = MAX_APP_MANAGER_SIZE)
    private final List<@MemberId String> managers;

}
