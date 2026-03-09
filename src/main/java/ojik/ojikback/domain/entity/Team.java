package ojik.ojikback.domain.entity;

public class Team {
    private Long id;
    private String name;

    public static Team create(String name) {
        Team team = new Team();
        team.name = name;
        return team;
    }

    public static Team restore(Long id, String name) {
        Team team = create(name);
        team.id = id;
        return team;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
