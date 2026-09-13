package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author JOE
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ...
     * init
     * add
     * commit
     * rm
     * log
     * global-log
     * find
     * status
     * checkout
     * branch
     * rm-branch
     * reset
     * merge
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            System.out.print("Please enter a command.");
            System.exit(0);
        }


        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                break;
            // TODO: FILL THE REST IN
            case "commit":

                break;
            case "rm":

                break;
            case "log":

                break;
            case "global-log":

                break;
            case "find":

                break;
            case "status":

                break;
            case "checkout":

                break;
            case "branch":

                break;
            case "rm-branch":

                break;
            case "reset":

                break;
            case "merge":

                break;
            default:
                System.out.print("No command with that name exists.");
                System.exit(0);

        }
    }
}
