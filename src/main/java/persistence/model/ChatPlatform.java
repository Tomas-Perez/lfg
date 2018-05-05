package persistence.model;

import javax.persistence.*;
import java.util.Set;

/**
 * @author Tomas Perez Molina
 */

@Entity
@Table(name = "chat_platform")
public class ChatPlatform {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "image")
    private String image;

    @ManyToMany(mappedBy = "chatPlatforms", cascade = CascadeType.PERSIST)
    private Set<Post> posts;

    public ChatPlatform(int id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public ChatPlatform() {
    }
}
