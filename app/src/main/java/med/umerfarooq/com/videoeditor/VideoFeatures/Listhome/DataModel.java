package med.umerfarooq.com.videoeditor.VideoFeatures.Listhome;

/**
 * Created by Umerfarooq on 4/13/2018.
 */

public class DataModel
    {

        public String icon;
        public String title;
        public String descriptiob;

        public DataModel(String icon,String title,String descriptiob)
        {
            this.icon = icon;
            this.title = title;
            this.descriptiob = descriptiob;
        }

        public String getIcon()
        {
            return icon;
        }

        public void setIcon(String icon)
        {
            this.icon = icon;
        }

        public String getTitle()
        {
            return title;
        }

        public void setTitle(String title)
        {
            this.title = title;
        }

        public String getDescriptiob()
        {
            return descriptiob;
        }

        public void setDescriptiob(String descriptiob)
        {
            this.descriptiob = descriptiob;
        }
    }
