package tlog16rs.resources.RBobjects;

/**
 * 
 * Created to help the designated endpoint's object handling.
 * <br>
 * <br> The getters, setters, contructor are generated through Lombok
 * <br> @see <a href="https://projectlombok.org/">https://projectlombok.org/</a>
 * 
 * @author Gyapi
 */
@lombok.NoArgsConstructor
@lombok.Getter
@lombok.Setter
public class FinishTaskRB {

    int year;
    int month;
    int day;
    String taskId;
    String startTime;
    String endTime;    
}
