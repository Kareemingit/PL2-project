package GymSystem.CoachSys;
import java.time.LocalDateTime;
import GymSystem.SystemEntity;
import GymSystem.Database;

//-------------------atts-----------------
public class Message extends SystemEntity{
    private int mssgId;
    private int sndrId;
    private int rcivrId;
    private LocalDateTime mssgDate;
    private String mssgContent;
    //---------------const-----------------
    public Message(int mssgId, int sndrId, int rcivrId,
                   LocalDateTime mssgDate, String mssgContent){
        this.mssgContent=mssgContent;
        this.mssgDate=mssgDate;
        this.mssgId=mssgId;
        this.rcivrId=rcivrId;
        this.sndrId=sndrId;
    }
//-------------------get----------------
    public int getMssgId() {
        return mssgId;
    }

    public int getRcivrId() {
        return rcivrId;
    }

    public int getSndrId() {
        return sndrId;
    }

    public LocalDateTime getMssgDate() {
        return mssgDate;
    }
    public String getMssgContent() {
        return mssgContent;
    }
//-----------set------------------------------

    public void setMssgContent(String mssgContent) {
        this.mssgContent = mssgContent;
    }
    public void setMssgId(int mssgId) {
        this.mssgId = mssgId;
    }
    //--------------toCsv----------------------------------
public String toCSVstring(){
        return String.format("%d,%d,%d,%s,%s",mssgId,sndrId,rcivrId,
                GymSystem.GymManagmentSystem.DATE_FMT.format(mssgDate.toLocalDate()),
                Database.escape(mssgContent));
}
}
