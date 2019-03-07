package com.gavin.cloud.auth.transformer;

import com.gavin.cloud.common.base.subject.Subject;
import com.gavin.cloud.sys.api.model.User;

import java.util.Objects;

public abstract class UserTransformer {

    public static Subject userToSubject(User user) {
        User activeUser = Objects.requireNonNull(user);
        Subject subject = new Subject();
        subject.setId(activeUser.getId());
        subject.setUsername(activeUser.getUsername());
        subject.setNickname(activeUser.getNickname());
        subject.setPhone(activeUser.getPhone());
        subject.setEmail(activeUser.getEmail());
        subject.setLangKey(activeUser.getLangKey());
        subject.setAvatar(activeUser.getAvatar());
        subject.setActivated(activeUser.getActivated());
        subject.setResetDate(activeUser.getResetDate());
        subject.setCreatedBy(activeUser.getCreatedBy());
        subject.setCreatedDate(activeUser.getCreatedDate());
        subject.setLastModifiedBy(activeUser.getLastModifiedBy());
        subject.setLastModifiedDate(activeUser.getLastModifiedDate());
        return subject;
    }

}
