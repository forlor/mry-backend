package com.mryqr.core.submission.domain.task;

import com.mryqr.common.domain.task.RetryableTask;
import com.mryqr.core.qr.domain.QrRepository;
import com.mryqr.core.submission.domain.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SyncSubmissionPlateFromQrTask implements RetryableTask {
    private final QrRepository qrRepository;
    private final SubmissionRepository submissionRepository;

    public void run(String qrId) {
        qrRepository.byIdOptional(qrId).ifPresent(qr -> {
            int count = submissionRepository.syncPlateFromQr(qr);
            log.debug("Synced plate ids for all {} submissions with qr[{}].", count, qrId);
        });
    }
}
