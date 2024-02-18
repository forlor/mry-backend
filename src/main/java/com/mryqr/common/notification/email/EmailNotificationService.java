package com.mryqr.common.notification.email;

import com.mryqr.common.notification.NotificationService;
import com.mryqr.common.notification.wx.Response;
import com.mryqr.common.notification.wx.ValueItem;
import com.mryqr.common.notification.wx.WxTemplateMessage;
import com.mryqr.common.wx.accesstoken.WxAccessTokenService;
import com.mryqr.core.app.domain.App;
import com.mryqr.core.app.domain.page.Page;
import com.mryqr.core.app.domain.page.setting.notification.NotificationRole;
import com.mryqr.core.assignment.domain.Assignment;
import com.mryqr.core.common.properties.PropertyService;
import com.mryqr.core.common.properties.WxProperties;
import com.mryqr.core.group.domain.Group;
import com.mryqr.core.group.domain.GroupRepository;
import com.mryqr.core.member.domain.Member;
import com.mryqr.core.member.domain.MemberRepository;
import com.mryqr.core.qr.domain.QrRepository;
import com.mryqr.core.submission.domain.Submission;
import com.mryqr.core.submission.domain.SubmissionApproval;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.mryqr.common.notification.wx.WxTemplateMessage.valueItemOf;
import static com.mryqr.core.app.domain.page.setting.notification.NotificationRole.*;
import static com.mryqr.core.common.utils.MryConstants.MRY_DATE_TIME_FORMATTER;
import static java.util.Collections.singleton;
import static java.util.Set.copyOf;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Component
@Profile("prod")
@RequiredArgsConstructor
public class EmailNotificationService implements NotificationService {
    private final WxProperties wxProperties;
    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final QrRepository qrRepository;
    private final RestTemplate restTemplate;
    private final WxAccessTokenService wxAccessTokenService;
    private final PropertyService propertyService;

    private final JavaMailSender mailSender;

    @Override
    public void notifyOnSubmissionCreated(Submission submission, Page page, App app, List<NotificationRole> notifyRoles) {
        Group group = groupRepository.cachedById(submission.getGroupId());
        Set<String> toBeNotifiedEmails = toBeNotifiedEmailsForSubmission(app, group, notifyRoles, submission.getCreatedBy());
        if (isEmpty(toBeNotifiedEmails)) {
            return;
        }

        String url = submissionUrlOf(submission.getId(), page.getId(), submission.getPlateId());
        String qrName = qrRepository.qrNameOf(submission.getQrId());
        String submittedBy = isNotBlank(submission.getCreatedBy()) ? memberRepository.cachedMemberNameOf(submission.getCreatedBy()) : "匿名";
        String createdAt = MRY_DATE_TIME_FORMATTER.format(submission.getCreatedAt());
        toBeNotifiedEmails.forEach(mail -> {
            try {
                MimeMessage mailMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mailMessage, "utf-8");
                String html = "<div style=\"margin-bottom:12px;\">您有新的表单提交，详情如下：</div>\n" +
                              "<div style=\"padding-left:16px;margin-bottom:5px;\">\n" +
                              "  <span style=\"color:#909399;padding-right:10px;\">" + app.instanceDesignation() + "名称：</span>" + qrName + "\n" +
                              "</div>\n" +
                              "<div style=\"padding-left:16px;margin-bottom:5px;\">\n" +
                              "  <span style=\"color:#909399;padding-right:10px;\">所在" + app.groupDesignation() + "：</span>" + group.getName() + "\n" +
                              "</div>\n" +
                              "<div style=\"padding-left:16px;margin-bottom:5px;\">\n" +
                              "  <span style=\"color:#909399;padding-right:10px;\">所在应用：</span>" + app.getName() + "\n" +
                              "</div>\n" +
                              "<div style=\"padding-left:16px;margin-bottom:5px;\">\n" +
                              "  <span style=\"color:#909399;padding-right:10px;\">提交时间：</span>" + createdAt + "\n" +
                              "</div>\n" +
                              "<div style=\"padding-left:16px;\">\n" +
                              "  <span style=\"color:#909399;padding-right:10px;\">提交人：</span>" + submittedBy + "\n" +
                              "</div>\n" +
                              "<div style=\"margin-top:12px;\">如需查看表单详情，请点击<a href=\"" + url + "\" target=\"_blank\">此链接</a>。</div>\n";

                helper.setText(html, true);
                helper.setTo(mail);
                helper.setSubject("您有新的表单提交，请关注。");
                helper.setFrom("码如云 <noreply@directmail.mryqr.com>");
                mailSender.send(mailMessage);
            } catch (Throwable t) {
                log.warn("Failed to send submission creation email for submission[{}].", submission.getId());
            }
        });

    }

    @Override
    public void notifyOnSubmissionUpdated(Submission submission, Page page, App app, List<NotificationRole> notifyRoles) {
        Group group = groupRepository.cachedById(submission.getGroupId());
        Set<String> toBeNotifiedOpenIds = toBeNotifiedEmailsForSubmission(app, group, notifyRoles, submission.getUpdatedBy());
        if (isEmpty(toBeNotifiedOpenIds)) {
            return;
        }

        String url = submissionUrlOf(submission.getId(), page.getId(), submission.getPlateId());
        String qrName = qrRepository.qrNameOf(submission.getQrId());
        String updatedBy = isNotBlank(submission.getUpdatedBy()) ? memberRepository.cachedMemberNameOf(submission.getUpdatedBy()) : null;
        Instant updatedAt = submission.getUpdatedAt();

        toBeNotifiedOpenIds.forEach(openId -> {
            WxTemplateMessage message = createNotifyOnUpdateMessage(openId, url, page, qrName, group.getName(), app.getName(), updatedBy, updatedAt);
            sendTemplateMessage(message);
        });
    }

    private Set<String> toBeNotifiedEmailsForSubmission(App app, Group group, List<NotificationRole> notifyRoles, String memberId) {
        Set<String> toBeNotifiedEmails = new HashSet<>();
        if (notifyRoles.contains(APP_MANAGER)) {
            toBeNotifiedEmails.addAll(memberRepository.cachedEmailsOf(app.getTenantId(), app.getManagers()).values());
        }

        if (notifyRoles.contains(GROUP_MANAGER)) {
            toBeNotifiedEmails.addAll(memberRepository.cachedEmailsOf(app.getTenantId(), group.getManagers()).values());
        }

        if (notifyRoles.contains(SUBMITTER) && isNotBlank(memberId)) {
            toBeNotifiedEmails.addAll(memberRepository.cachedEmailsOf(app.getTenantId(), List.of(memberId)).values());
        }

        toBeNotifiedEmails.removeAll(singleton(null));
        return toBeNotifiedEmails;
    }


    private WxTemplateMessage createNotifyOnUpdateMessage(String openId,
                                                          String url,
                                                          Page page,
                                                          String qrName,
                                                          String groupName,
                                                          String appName,
                                                          String updatedBy,
                                                          Instant updatedAt) {
        return WxTemplateMessage.builder()
                .touser(openId)
                .template_id(wxProperties.getSubmissionUpdatedTemplateId())
                .url(url)
                .data(Map.of("first", valueItemOf(qrName),
                        "thing2", valueItemOf(page.getSetting().getPageName()),
                        "thing7", valueItemOf(appName),
                        "thing6", valueItemOf(groupName),
                        "time3", valueItemOf(MRY_DATE_TIME_FORMATTER.format(updatedAt)),
                        "thing4", valueItemOf(updatedBy),
                        "remark", valueItemOf("表单已更新，点击可查看详情")
                ))
                .build();
    }

    @Override
    public void notifySubmitterOnSubmissionApproved(Submission submission,
                                                    Page page,
                                                    App app,
                                                    SubmissionApproval approval) {
        if (isBlank(submission.getCreatedBy())) {
            return;
        }

        memberRepository.cachedByIdOptional(submission.getCreatedBy()).stream()
                .filter(member -> isNotBlank(member.getMobileWxOpenId()))
                .findFirst().ifPresent(member -> {
                    WxTemplateMessage message = createNotifyOnApprovalMessage(submission, page, approval, member, app.getName());
                    sendTemplateMessage(message);
                });
    }

    private WxTemplateMessage createNotifyOnApprovalMessage(Submission submission,
                                                            Page page,
                                                            SubmissionApproval approval,
                                                            Member submitter,
                                                            String appName) {
        String url = submissionUrlOf(submission.getId(), page.getId(), submission.getPlateId());
        String qrName = qrRepository.qrNameOf(submission.getQrId());
        String approvedBy = isNotBlank(approval.getApprovedBy()) ? memberRepository.cachedMemberNameOf(approval.getApprovedBy()) : null;
        Group group = groupRepository.cachedById(submission.getGroupId());

        return WxTemplateMessage.builder()
                .touser(submitter.getMobileWxOpenId())
                .template_id(wxProperties.getSubmissionApprovedTemplateId())
                .url(url)
                .data(Map.of("first", valueItemOf(qrName),
                        "thing2", valueItemOf(page.getSetting().getPageName()),
                        "thing8", valueItemOf(appName),
                        "thing9", valueItemOf(group.getName()),
                        "phrase10", approveResult(page, approval),
                        "thing6", valueItemOf(approvedBy),
                        "remark", valueItemOf("点击可查看详情")
                ))
                .build();
    }

    private ValueItem approveResult(Page page, SubmissionApproval approval) {
        return approval.isPassed() ?
                valueItemOf(page.approvalPassText(), "#10B01B") :
                valueItemOf(page.approvalNotPassText(), "#EA0000");
    }

    @Override
    public void notifyOperatorsAssignmentNearExpire(Assignment assignment, App app) {
        List<String> operators = assignment.getOperators();
        Set<String> operatorWxOpenIds = copyOf(memberRepository.cachedMobileWxOpenIdsOf(assignment.getTenantId(), operators).values());
        if (isEmpty(operatorWxOpenIds)) {
            return;
        }

        String url = propertyService.clientBaseUrl() + "/operations/" + assignment.getAppId() + "/my-assignments/" + assignment.getId() + "/qrs";

        operatorWxOpenIds.forEach(openId -> {
            WxTemplateMessage message = createNotifyOnAssignmentNearExpireMessage(openId, url, assignment, app);
            sendTemplateMessage(message);
        });
    }

    private WxTemplateMessage createNotifyOnAssignmentNearExpireMessage(String openId,
                                                                        String url,
                                                                        Assignment assignment,
                                                                        App app) {
        return WxTemplateMessage.builder()
                .touser(openId)
                .template_id(wxProperties.getAssignmentNearExpireTemplateId())
                .url(url)
                .data(Map.of("first", valueItemOf("您有任务即将到期"),
                        "thing2", valueItemOf(assignment.getName()),
                        "time5", valueItemOf(MRY_DATE_TIME_FORMATTER.format(assignment.getStartAt())),
                        "time3", valueItemOf(MRY_DATE_TIME_FORMATTER.format(assignment.getExpireAt())),
                        "thing6", valueItemOf(app.getName()),
                        "remark", valueItemOf("点击可查看任务详情")
                ))
                .build();
    }

    @Override
    public void notifyOperatorsOnAssignmentCreated(Assignment assignment, App app) {
        List<String> operators = assignment.getOperators();
        Set<String> operatorWxOpenIds = copyOf(memberRepository.cachedMobileWxOpenIdsOf(assignment.getTenantId(), operators).values());
        if (isEmpty(operatorWxOpenIds)) {
            return;
        }

        String url = propertyService.clientBaseUrl() + "/operations/" + assignment.getAppId() + "/my-assignments/" + assignment.getId() + "/qrs";

        operatorWxOpenIds.forEach(openId -> {
            WxTemplateMessage message = createNotifyOnAssignmentCreatedMessage(openId, url, assignment, app);
            sendTemplateMessage(message);
        });
    }

    private WxTemplateMessage createNotifyOnAssignmentCreatedMessage(String openId,
                                                                     String url,
                                                                     Assignment assignment,
                                                                     App app) {
        return WxTemplateMessage.builder()
                .touser(openId)
                .template_id(wxProperties.getAssignmentNearExpireTemplateId())
                .url(url)
                .data(Map.of("first", valueItemOf("您有新任务需要完成"),
                        "thing2", valueItemOf(assignment.getName()),
                        "time5", valueItemOf(MRY_DATE_TIME_FORMATTER.format(assignment.getStartAt())),
                        "time3", valueItemOf(MRY_DATE_TIME_FORMATTER.format(assignment.getExpireAt())),
                        "thing6", valueItemOf(app.getName()),
                        "remark", valueItemOf("点击可查看任务详情")
                ))
                .build();
    }

    private String submissionUrlOf(String submissionId, String pageId, String plateId) {
        return propertyService.clientBaseUrl() + "/r/" + plateId + "/pages/" + pageId + "/" + submissionId;
    }

    private void sendTemplateMessage(WxTemplateMessage message) {
        if (!wxProperties.isMobileWxEnabled()) {
            return;
        }

        try {
            String accessToken = wxAccessTokenService.getAccessToken();
            String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken;
            Response response = restTemplate.postForObject(url, message, Response.class);
            if (response == null || response.getErrcode() != 0) {
                log.error("Failed to send wx template message[{}] with error[{}].", message, response);
            }
        } catch (Throwable t) {
            log.error("Failed to send wx template message[{}].", message, t);
        }
    }

}
