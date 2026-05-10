package ds.agh.rabbitmq.rabbitmqsystem.message;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum GoodType implements Serializable {
    PEOPLE("people"),
    CARGO("cargo"),
    SATELLITE("satellite");

    final String name;

    GoodType(String name) {
        this.name = name;
    }
}
