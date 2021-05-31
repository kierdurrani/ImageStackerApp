package stacker;

import java.util.ArrayList;


public class ProgressBar {

    // Thread safe way of calculating percent for big task made of lots of smaller ones:
    // percent complete is either calculated from this field, or from subtasks going recursively.

    private final String TaskName;
    private int percent = 0;
    private ProgressBar parentTask;

    public ArrayList<ProgressBar> subTaskList; // this will cause issues if there are loops

    public ProgressBar(String taskName) {
        TaskName = taskName;
    }

    public int getPercent()
    {
        if(subTaskList == null){
            return percent;
        }else
        {
            int percentSum = 0;
            for( ProgressBar subTask : subTaskList )
            {
                percentSum += subTask.getPercent();
            }

            return(percentSum/subTaskList.size());
        }
    }

    public String printPercent(){
        System.out.println(TaskName + ": " + getPercent() + "% complete. ");
        return (TaskName + ": " + getPercent() + "% complete. ");
    }

    public void populateSubTaskList(int numberOfSubtasks, String subtaskName){

        subTaskList = new ArrayList<ProgressBar>(numberOfSubtasks);

        for(int i = 0; i < numberOfSubtasks; i++)
        {
            ProgressBar child = new ProgressBar(subtaskName + " " + (i + 1) + " of " + numberOfSubtasks );
            child.parentTask = this;
            subTaskList.add(child);

        }

    }

    public void setProgressPercent(int x)
    {
        if(subTaskList != null)
        {
            System.out.println("Warning - called setProgressPercent on parent task when subtasks are present!");
        }

        percent = x;
    }




}
