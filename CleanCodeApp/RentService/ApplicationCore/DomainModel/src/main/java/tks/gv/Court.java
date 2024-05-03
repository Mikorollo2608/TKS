package tks.gv;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Court {
    @Setter(AccessLevel.NONE)
    private UUID id;

    private double area;
    private int baseCost;
    private int courtNumber;

    private boolean archive = false;
    private boolean rented = false;

    public Court(UUID id, double area, int baseCost, int courtNumber) {
        this.id = id;
        this.area = area;
        this.baseCost = baseCost;
        this.courtNumber = courtNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Court court = (Court) o;
        return Objects.equals(id, court.id);
    }
}
