package tks.gv.rentservice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
import java.util.Objects;

@NoArgsConstructor
public class Client {
    private enum ClientType {
        NORMAL(0, 3), ATHLETE(0.1, 6), COACH(0.2, 12);

        private final double discount;
        @Getter
        private final int maxHours;
        ClientType(double discount, int maxHours) {
            this.discount = discount;
            this.maxHours = maxHours;
        }

        public double applyDiscount() {
            return discount;
        }

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    @Getter
    private UUID id;
    @Setter
    @Getter
    private String login;
    @Setter
    @Getter
    private boolean archive = false;

    private ClientType clientType;
    @Getter
    private String clientTypeName;

    public Client(UUID id, String login, String clientType) {
        this.id = id;
        this.login = login;

        if (clientType != null) {
            this.clientType = switch (clientType.toLowerCase()) {
                case "athlete" -> ClientType.ATHLETE;
                case "coach" -> ClientType.COACH;
                default -> ClientType.NORMAL;
            };
        } else {
            this.clientType = ClientType.NORMAL;
        }
        this.clientTypeName = this.clientType.toString();
    }

    public double applyDiscount() {
        return clientType.applyDiscount();
    }

    public int clientMaxHours() {
        return clientType.getMaxHours();
    }

    public void setClientTypeName(String clientType) {
        if (clientType != null) {
            this.clientType = switch (clientType.toLowerCase()) {
                case "athlete" -> ClientType.ATHLETE;
                case "coach" -> ClientType.COACH;
                default -> ClientType.NORMAL;
            };
        } else {
            this.clientType = ClientType.NORMAL;
        }
        this.clientTypeName = this.clientType.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(id, client.id);
    }

}
