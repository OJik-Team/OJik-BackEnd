package ojik.ojikback.domain.entity;

public class Team {
    private Long id;
    private String name;
    private String shortName;
    private String themeColor;

    public static Team create(String name, String shortName, String themeColor) {
        Team team = new Team();
        team.name = name;
        team.shortName = shortName;
        team.themeColor = themeColor;
        return team;
    }

    public static Team restore(Long id, String name, String shortName, String themeColor) {
        Team team = create(name, shortName, themeColor);
        team.id = id;
        return team;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public String getThemeColor() {
        return themeColor;
    }
}
