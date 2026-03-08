package ojik.ojikback.domain.entity;

import java.math.BigDecimal;

public class Stadium {
    private Long id;
    private String name;
    private String city;
    private BigDecimal latitude;
    private BigDecimal longitude;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }
}
