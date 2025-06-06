package ${packageName}.${packageClass}.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "${tableName}")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ${entityName}{

    @Id
    private Long id;
${fields}

}