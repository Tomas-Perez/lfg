package persistence.model;

import javax.persistence.*;
import java.util.Set;

/**
 * @author Tomas Perez Molina
 */

@Entity
@Table(name = "game_platform")
public class GamePlatform {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "image")
    private String image;

    @ManyToMany(mappedBy = "gamePlatforms", cascade = CascadeType.PERSIST)
    private Set<Post> posts;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "game_in_platform",
            joinColumns = @JoinColumn(name = "game_platform_id"),
            inverseJoinColumns = @JoinColumn(name = "game_id"))
    private Set<Game> games;


}
