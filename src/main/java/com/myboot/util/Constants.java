package com.myboot.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {
    public static final String TOPIC_FOR_SENDING = "my_topic";
    public static final String TOPIC_FOR_SENDING_BATCH = "my_topic_batch";
    public static final String REPLY_TOPIC_FOR_SENDING = "Reply_Topic";
    public static final String REPLY_TOPIC_FOR_SENDING_LIST = "Reply_Topic_List";
    public static final String TRUSTED_PACKAGES ="com.myboot.*";
}
