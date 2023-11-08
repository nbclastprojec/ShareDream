import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.io.Serializable

data class AlarmPost(
    val uid: String,
    val title: String,
    var imgs: List<String>,
    val nickname: String,
    val timestamp: Timestamp,
    val documentId: String
) : Serializable {

    constructor() : this(
        "", "", listOf(), "", Timestamp.now(),""
    )
}
