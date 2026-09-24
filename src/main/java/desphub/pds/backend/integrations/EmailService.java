package desphub.pds.backend.integrations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final RestClient http;
    private final String apiKey;
    private final String from;

    public EmailService(
            @Value("${resend.api-key:}") String apiKey,
            @Value("${resend.from:DespHub <onboarding@resend.dev>}") String from
    ) {
        this.apiKey = apiKey;
        this.from = from;
        this.http = RestClient.builder()
                .baseUrl("https://api.resend.com")
                .build();
    }

    public void sendPasswordReset(String toEmail, String toName, String link) {
        String subject = "DespHub — defina sua senha";

        String html = """
                <table
                    border="0"
                    width="100%%"
                    cellpadding="0"
                    cellspacing="0"
                    role="presentation"
                    align="center"
                >
                    <tbody>
                        <tr>
                            <td
                                dir="ltr"
                                lang="pt-BR"
                                style="
                                    font-family: -apple-system, BlinkMacSystemFont, &#x27;Segoe UI&#x27;,
                                        &#x27;Roboto&#x27;, &#x27;Oxygen&#x27;, &#x27;Ubuntu&#x27;,
                                        &#x27;Cantarell&#x27;, &#x27;Fira Sans&#x27;, &#x27;Droid Sans&#x27;,
                                        &#x27;Helvetica Neue&#x27;, sans-serif;
                                    font-size: 14px;
                                    min-height: 100%%;
                                    line-height: 155%%;
                                    background-color: #F1F3F6;
                                "
                            >
                                <table
                                    align="center"
                                    width="100%%"
                                    border="0"
                                    cellpadding="0"
                                    cellspacing="0"
                                    role="presentation"
                                    style="
                                        max-width: 600px;
                                        background-color: #FFFFFF;
                                        width: 100%%;
                                        align: center;
                                        border-radius: 22px;
                                        border-width: 1px;
                                        border-color: #E7EAEF;
                                        border-style: solid;
                                    "
                                >
                                    <tbody>
                                        <tr style="width: 100%%">
                                            <td
                                                style="
                                                    padding-top: 56px;
                                                    padding-right: 48px;
                                                    padding-bottom: 56px;
                                                    padding-left: 48px;
                                                "
                                            >
                                                <!-- Logo -->
                                                <table
                                                    align="center"
                                                    width="100%%"
                                                    border="0"
                                                    cellpadding="0"
                                                    cellspacing="0"
                                                    role="presentation"
                                                >
                                                    <tbody style="width: 100%%">
                                                        <tr style="width: 100%%">
                                                            <td
                                                                align="center"
                                                                data-id="__react-email-column"
                                                            >
                                                                <a
                                                                    href="https://desphub.com.br/"
                                                                    style="
                                                                        color: #067df7;
                                                                        text-decoration-line: none;
                                                                    "
                                                                    target="_blank"
                                                                >
                                                                    <img
                                                                        alt="Logo da DespHub com um D azul e um ícone de seta laranja dentro, seguido pelo texto &quot;DespHub&quot; em azul e laranja"
                                                                        height="106"
                                                                        src="https://resend-attachments.s3.amazonaws.com/ELqKS4IyXa6nBWTs3j58r2/7b1d595a-60d6-4ca2-b811-039ed8d6309a"
                                                                        style="
                                                                            display: block;
                                                                            outline: none;
                                                                            border: none;
                                                                            text-decoration: none;
                                                                            padding-top: 0px;
                                                                            padding-right: 0px;
                                                                            padding-bottom: 0px;
                                                                            padding-left: 10px;
                                                                        "
                                                                        width="465"
                                                                    />
                                                                </a>
                                                            </td>
                                                        </tr>
                                                    </tbody>
                                                </table>

                                                <!-- Título -->
                                                <h1
                                                    style="
                                                        margin: 0;
                                                        padding: 0;
                                                        font-size: 32px;
                                                        line-height: 120%%;
                                                        padding-top: 0.389em;
                                                        font-weight: 700;
                                                        color: #0B1929;
                                                        margin-top: 0;
                                                        margin-bottom: 16px;
                                                        letter-spacing: -0.01em;
                                                        text-align: center;
                                                    "
                                                >
                                                    Olá, %s!
                                                </h1>

                                                <!-- Descrição -->
                                                <p
                                                    style="
                                                        margin: 0;
                                                        padding: 0;
                                                        font-size: 15px;
                                                        padding-top: 0.5em;
                                                        padding-bottom: 0.5em;
                                                        color: #5A6472;
                                                        line-height: 160%%;
                                                        margin-top: 0;
                                                        margin-bottom: 36px;
                                                        text-align: center;
                                                    "
                                                >
                                                    Use o botão abaixo para redefinir sua senha de acesso
                                                    ao DespHub. Se você ou seu administrador não solicitaram
                                                    a troca de senha, pode apenas ignorar.
                                                </p>

                                                <!-- Botão -->
                                                <table
                                                    align="center"
                                                    width="100%%"
                                                    border="0"
                                                    cellpadding="0"
                                                    cellspacing="0"
                                                    role="presentation"
                                                >
                                                    <tbody style="width: 100%%">
                                                        <tr style="width: 100%%">
                                                            <td
                                                                align="center"
                                                                data-id="__react-email-column"
                                                            >
                                                                <a
                                                                    class="button"
                                                                    href="%s"
                                                                    style="
                                                                        line-height: 100%%;
                                                                        text-decoration: none;
                                                                        display: inline-block;
                                                                        max-width: 100%%;
                                                                        mso-padding-alt: 0px;
                                                                        margin: 0;
                                                                        padding: 0;
                                                                        box-sizing: border-box;
                                                                        padding-top: 14px;
                                                                        padding-right: 28px;
                                                                        padding-bottom: 14px;
                                                                        padding-left: 28px;
                                                                        background-color: #E76B16;
                                                                        color: #FFFFFF;
                                                                        border-radius: 12px;
                                                                        font-weight: 600;
                                                                        font-size: 15px;
                                                                        text-align: center;
                                                                    "
                                                                    target="_blank"
                                                                >
                                                                    <span>
                                                                        <!--[if mso]>
                                                                            <i
                                                                                style="
                                                                                    mso-font-width: 466.6666666666667%%;
                                                                                    mso-text-raise: 21px;
                                                                                "
                                                                                hidden
                                                                            >
                                                                                &#8202;&#8202;&#8202;
                                                                            </i>
                                                                        <![endif]-->
                                                                    </span>

                                                                    <span
                                                                        style="
                                                                            max-width: 100%%;
                                                                            display: inline-block;
                                                                            line-height: 120%%;
                                                                            mso-padding-alt: 0px;
                                                                            mso-text-raise: 10.5px;
                                                                        "
                                                                    >
                                                                        Redefinir Senha
                                                                    </span>

                                                                    <span>
                                                                        <!--[if mso]>
                                                                            <i
                                                                                style="
                                                                                    mso-font-width: 466.6666666666667%%;
                                                                                "
                                                                                hidden
                                                                            >
                                                                                &#8202;&#8202;&#8202;&#8203;
                                                                            </i>
                                                                        <![endif]-->
                                                                    </span>
                                                                </a>
                                                            </td>
                                                        </tr>
                                                    </tbody>
                                                </table>

                                                <!-- Link alternativo -->
                                                <p
                                                    style="
                                                        margin: 0;
                                                        padding: 0;
                                                        font-size: 13px;
                                                        padding-top: 0.5em;
                                                        padding-bottom: 0.5em;
                                                        color: #5A6472;
                                                        line-height: 160%%;
                                                        margin-top: 36px;
                                                        margin-bottom: 0;
                                                        text-align: center;
                                                    "
                                                >
                                                    Botão não funciona?

                                                    <a
                                                        href="%s"
                                                        rel="noopener noreferrer nofollow"
                                                        style="
                                                            color: #B4520C;
                                                            text-decoration-line: none;
                                                            text-decoration: underline;
                                                        "
                                                        target="_blank"
                                                    >
                                                        <u>Use esse link</u>
                                                    </a>.
                                                </p>

                                                <!-- Rodapé -->
                                                <table
                                                    align="center"
                                                    width="100%%"
                                                    border="0"
                                                    cellpadding="0"
                                                    cellspacing="0"
                                                    role="presentation"
                                                    class="node-footer"
                                                    style="font-size: 0.8em"
                                                >
                                                    <tbody>
                                                        <tr>
                                                            <td style="padding-top: 40px">
                                                                <hr
                                                                    class="divider"
                                                                    style="
                                                                        width: 100%%;
                                                                        border: none;
                                                                        border-color: #E7EAEF;
                                                                        border-top: 1px solid #eaeaea;
                                                                        padding-bottom: 1em;
                                                                        border-style: solid;
                                                                        border-width: 0;
                                                                        border-top-width: 1px;
                                                                        margin-top: 0;
                                                                        margin-bottom: 20px;
                                                                    "
                                                                />

                                                                <p
                                                                    style="
                                                                        margin: 0;
                                                                        padding: 0;
                                                                        font-size: 12px;
                                                                        padding-top: 0.5em;
                                                                        padding-bottom: 0.5em;
                                                                        color: #5A6472;
                                                                        line-height: 160%%;
                                                                        margin-top: 0;
                                                                        margin-bottom: 0;
                                                                        text-align: center;
                                                                    "
                                                                >
                                                                    Feito com carinho :)<br />

                                                                    <a
                                                                        href="https://unsubscribe.resend.com/test/?token=eyJhbGciOiJIUzI1NiJ9.eyJ0ZWFtSWQiOiJlOWE3YTM3Zi0yMzZlLTQzZTUtODZhNS1iMGZhZjUwOTE1OTAifQ.Yj6lv1jihYH_wGxaxDl7QZc2cfTD5RlwDHsrBKr4wG4"
                                                                        rel="noopener noreferrer nofollow"
                                                                        ses:no-track="true"
                                                                        style="
                                                                            color: #B4520C;
                                                                            text-decoration-line: none;
                                                                            text-decoration: underline;
                                                                        "
                                                                        target="_blank"
                                                                    >
                                                                        Unsubscribe
                                                                    </a>

                                                                    •
                                                                    <!-- -->
                                                                    ©
                                                                    <!-- -->
                                                                    2026 DespHub
                                                                </p>
                                                            </td>
                                                        </tr>
                                                    </tbody>
                                                </table>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </td>
                        </tr>
                    </tbody>
                </table>
                """.formatted(escape(toName), link, link);

        send(toEmail, subject, html);
    }

    private void send(String toEmail, String subject, String html) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn(
                    "[DEV] Resend não configurado (resend.api-key vazio). "
                    + "E-mail para {} não enviado. Link/conteúdo: {}",
                    toEmail,
                    html
            );
            return;
        }

        try {
            http.post()
                    .uri("/emails")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(
                            Map.of(
                                    "from", from,
                                    "to", List.of(toEmail),
                                    "subject", subject,
                                    "html", html
                            )
                    )
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error(
                    "Falha ao enviar e-mail via Resend para {}",
                    toEmail,
                    e
            );

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Falha ao enviar o e-mail"
            );
        }
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
