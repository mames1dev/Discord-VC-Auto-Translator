package mames1.net.mamesosu.object;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;

@Setter @Getter
public class Translate {

    Member member;
    String lang;

    public Translate(Member member) {
        this.member = member;
    }
}
