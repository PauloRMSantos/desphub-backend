package desphub.pds.backend.jobs;

import desphub.pds.backend.repositories.IGeneratedDocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/** Remove periodicamente os documentos gerados mais antigos que o período de retenção. */
@Component
public class DocumentRetentionJob {

    private static final Logger log = LoggerFactory.getLogger(DocumentRetentionJob.class);

    private final IGeneratedDocumentRepository documents;
    private final long retentionDays;

    public DocumentRetentionJob(
            IGeneratedDocumentRepository documents,
            @Value("${desphub.documents.retention-days:30}") long retentionDays
    ) {
        this.documents = documents;
        this.retentionDays = retentionDays;
    }

    @Scheduled(cron = "${desphub.documents.cleanup-cron:0 0 3 * * *}")
    @Transactional
    public void purgeExpiredDocuments() {
        if (retentionDays <= 0) {
            return;
        }
        Instant cutoff = Instant.now().minus(retentionDays, ChronoUnit.DAYS);
        int removed = documents.deleteByCreatedAtBefore(cutoff);
        if (removed > 0) {
            log.info("Retenção de documentos: {} documento(s) com mais de {} dia(s) removido(s).",
                    removed, retentionDays);
        }
    }
}
