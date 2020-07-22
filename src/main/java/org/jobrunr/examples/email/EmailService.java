package org.jobrunr.examples.email;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import org.jobrunr.examples.employee.Employee;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.nio.file.Path;

@ApplicationScoped
public class EmailService {

    @Inject
    Mailer mailer;

    public void sendSalarySlip(Employee employee, Path salarySlipPath) {
        String body = String.format("<strong>Dear %s</strong>" + "\n" +
                "<p>Here you can find your weekly salary slip.</p>" +
                "<p>Thanks again for your hard work,<br/>Acme Corp</p>", employee.firstName);
        final Mail mail = Mail.withHtml(employee.email, "Your weekly salary slip", body)
                .addAttachment("Salary slip", salarySlipPath.toFile(), "application/pdf");
        mailer.send(mail);
    }
}
