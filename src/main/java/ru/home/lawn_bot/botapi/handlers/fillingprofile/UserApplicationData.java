package ru.home.lawn_bot.botapi.handlers.fillingprofile;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.Date;


@ToString
@Setter
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserApplicationData {
    private String secondName;
    private String name;
    private String phoneNumber;
    private String email;
    private Date date;
    private int howMuch;
    private int forHowLong;

    public UserApplicationData() {
    }

    public String getSecondName() {
        if (!(this.secondName == null)) {
            return this.secondName;
        } else {
            return "";
        }
    }

    public String getName() {
        if (!(this.name == null)) {
            return this.name;
        } else {
            return "";
        }
    }

    public String getPhoneNumber() {
        if (!(this.phoneNumber == null)) {
            return this.phoneNumber;
        } else {
            return "";
        }
    }

    public String getEmail() {
        if (!(this.email == null)) {
            return this.email;
        } else {
            return "";
        }
    }

    public Date getDate() {
        if (!(this.date == null)) {
            return this.date;
        } else {
            return new Date();
        }
    }

    public int getHowMuch() {
        return this.howMuch;
    }

    public int getForHowLong() {
        return this.forHowLong;
    }

}
