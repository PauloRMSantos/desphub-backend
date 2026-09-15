package desphub.pds.backend.dtos.detran;

public record Violations(
        ViolationSummary upcoming,
        ViolationSummary overdue,
        ViolationSummary suspended,
        ViolationSummary awaitingDefense,
        ViolationSummary awaitingJudgment
) {
}
