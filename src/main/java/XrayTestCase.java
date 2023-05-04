import java.util.ArrayList;

public class XrayTestCase {
    private String summary;
    private String assigneeID;
    private ArrayList<String> labels;
    private ArrayList<Step> Steps;

    public String getAssigneeID() {
        return assigneeID;
    }

    public void setAssigneeID(String assigneeID) {
        this.assigneeID = assigneeID;
    }

    public ArrayList<String> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }

    public ArrayList<Step> getSteps() {
        return Steps;
    }

    public void setSteps(ArrayList<Step> Steps) {
        this.Steps = Steps;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public static class Step {
        private String action = "";
        private String data = "";
        private String result = "";

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }
}
