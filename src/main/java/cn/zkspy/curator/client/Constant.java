package cn.zkspy.curator.client;

public abstract class Constant {
    /**
     * Description: <br>
     * 
     * @author XXX<br>
     * @version 8.0<br>
     * @taskId <br>
     * @CreateDate 2018年7月3日 <br>
     * @since V8<br>
     * @see cn.zkspy.gui <br>
     */
    public interface IconPath {
        String CONNECT = "icon/zkspy_connect.png";

        String DISCONNECT = "icon/zkspy_disconnect.png";

        String ABOUT = "icon/zkspy_about.png";

        String REFRESH = "icon/zkspy_refresh.png";
    }

    /**
     * Description: <br>
     * 
     * @author XXX<br>
     * @version 8.0<br>
     * @taskId <br>
     * @CreateDate 2018年7月3日 <br>
     * @since V8<br>
     * @see cn.zkspy.gui <br>
     */
    public interface MetaDataKey {
        String A_VERSION = "ACL Version";

        String C_TIME = "Creation Time";

        String C_VERSION = "Children Version";

        String CZXID = "Creation ID";

        String DATA_LENGTH = "Data Length";

        String EPHEMERAL_OWNER = "Ephemeral Owner";

        String M_TIME = "Last Modified Time";

        String MZXID = "Modified ID";

        String NUM_CHILDREN = "Number of Children";

        String PZXID = "Node ID";

        String VERSION = "Data Version";
    }

    /**
     * Description: <br>
     * 
     * @author XXX<br>
     * @version 8.0<br>
     * @taskId <br>
     * @CreateDate 2018年7月3日 <br>
     * @since V8<br>
     * @see cn.zkspy.gui <br>
     */
    public interface ACLDataKey {
        String ACL_PERMS = "Permissions";

        String ACL_SCHEME = "Scheme";

        String ACL_ID = "Id";
    }
}
