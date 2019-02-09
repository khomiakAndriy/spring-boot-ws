package com.springbootws.springbootws.shared;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.springbootws.springbootws.shared.dto.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AmazonSES {

    @Value("${hostUrl}")
    private String host;

    final String FROM = "khomiak.andriy@gmail.com";

    // The subject line for the email.
    final String SUBJECT = "One last step to complete your registration";

    final String PASSWORD_RESET_SUBJECT = "Password reset request";

    // The HTML body for the email.
    final String HTMLBODY = "<h1>Please verify your email address</h1>"
            + "<p>Thank you for registering with our mobile app. To complete registration process and be able to log in,"
            + " click on the following link: "
            + "<a href='http://%s:8080/verification-service/email-verification.jsp?token=%s'>"
            + "Final step to complete your registration" + "</a><br/><br/>"
            + "Thank you! And we are waiting for you inside!";

    // The email body for recipients with non-HTML email clients.
    final String TEXTBODY = "Please verify your email address. "
            + "Thank you for registering with our mobile app. To complete registration process and be able to log in,"
            + " open then the following URL in your browser window: "
            + " http://%s:8080/verification-service/email-verification.jsp?token=%s"
            + " Thank you! And we are waiting for you inside!";


    final String PASSWORD_RESET_HTMLBODY = "<h1>A request to reset your password</h1>"
            + "<p>Hi, %s!</p> "
            + "<p>Someone has requested to reset your password with our project. If it were not you, please ignore it."
            + " otherwise please click on the link below to set a new password: "
            + "<a href='http://%s:8080/verification-service/password-reset.jsp?token=%s'>"
            + " Click this link to Reset Password"
            + "</a><br/><br/>"
            + "Thank you!";

    // The email body for recipients with non-HTML email clients.
    final String PASSWORD_RESET_TEXTBODY = "A request to reset your password "
            + "Hi, %s! "
            + "Someone has requested to reset your password with our project. If it were not you, please ignore it."
            + " otherwise please open the link below in your browser window to set a new password:"
            + " http://%s:8080/verification-service/password-reset.jsp?token=%s"
            + " Thank you!";

    public void verifyEmail(UserDto userDto) {

        // You can also set your keys this way. And it will work!
        //System.setProperty("aws.accessKeyId", "<YOUR KEY ID HERE>");
        //System.setProperty("aws.secretKey", "<SECRET KEY HERE>");

        BasicAWSCredentials awsCreds = new BasicAWSCredentials("", "");
        AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard().withCredentials(new
                AWSStaticCredentialsProvider(awsCreds)).withRegion(Regions.EU_WEST_1)
                .build();

//        AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.EU_WEST_1)
//                .build();

        String htmlBodyWithToken = String.format(HTMLBODY, host, userDto.getEmailVerificationToken());
        String textBodyWithToken = String.format(TEXTBODY, host, userDto.getEmailVerificationToken());

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(userDto.getEmail()))
                .withMessage(new Message()
                        .withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(htmlBodyWithToken))
                                .withText(new Content().withCharset("UTF-8").withData(textBodyWithToken)))
                        .withSubject(new Content().withCharset("UTF-8").withData(SUBJECT)))
                .withSource(FROM);

        client.sendEmail(request);

        System.out.println("Email sent!");

    }

    public boolean sendPasswordResetRequest(String firstName, String email, String token)
    {
        boolean returnValue = false;

//        AmazonSimpleEmailService client =
//                AmazonSimpleEmailServiceClientBuilder.standard()
//                        .withRegion(Regions.US_EAST_1).build();

        BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIAITVM6NNJT33DYGFA", "kZSN7kh+JCUDSZIYTIfmn2dv+eEJEJFFy4Czk3YA");
        AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard().withCredentials(new
                AWSStaticCredentialsProvider(awsCreds)).withRegion(Regions.EU_WEST_1)
                .build();

        String htmlBodyWithToken = String.format(PASSWORD_RESET_HTMLBODY, firstName, host, token);

        String textBodyWithToken = String.format(PASSWORD_RESET_TEXTBODY, firstName, host, token);

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(
                        new Destination().withToAddresses( email ) )
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content()
                                        .withCharset("UTF-8").withData(htmlBodyWithToken))
                                .withText(new Content()
                                        .withCharset("UTF-8").withData(textBodyWithToken)))
                        .withSubject(new Content()
                                .withCharset("UTF-8").withData(PASSWORD_RESET_SUBJECT)))
                .withSource(FROM);

        SendEmailResult result = client.sendEmail(request);
        if(result != null && (result.getMessageId()!=null && !result.getMessageId().isEmpty()))
        {
            returnValue = true;
        }

        return returnValue;
    }

}
