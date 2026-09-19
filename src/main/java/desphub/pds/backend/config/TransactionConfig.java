package desphub.pds.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Torna o interceptor de transação EXTERNO (order 0) para que o TenantFilterAspect
 * (order padrão, interno) rode com a Session já aberta pela transação.
 */
@Configuration
@EnableTransactionManagement(order = 0)
public class TransactionConfig {
}
