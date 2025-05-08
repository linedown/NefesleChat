package ru.linedown.nefeslechat.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.linedown.nefeslechat.enums.MessageTypeEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private int fromUserId;

    private String senderName;

    private MessageTypeEnum type;

    private String message;

    private String lastMessageDate;

    private String groupUrl;

    private int groupId;

    private String fileUrl;

    private String fileName;

    private boolean isMessageSeen;
}
