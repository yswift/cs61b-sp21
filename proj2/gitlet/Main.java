package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author yswift
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            Utils.exitWithError("Please enter a command.");
        }

        String firstArg = args[0];
        if (firstArg.equals("init")) {
            validateNumArgs("init", args, 1);
            Repository.init();
            return;
        }
        validateDir();
        switch(firstArg) {
            case "add":
                // handle the `add [filename]` command
                validateNumArgs("add", args, 2);
                Repository.add(args[1]);
                break;
            case "commit":
                validateNumArgs("commit", args, 2);
//                System.out.println("commit: " + args[1]);
                Repository.commit(args[1]);
                break;
            case "rm":
                validateNumArgs("rm", args, 2);
                Repository.rm(args[1]);
                break;
            case "log":
//                validateNumArgs("log", args, 1);
                Repository.log();
                break;
            case "global-log":
//                validateNumArgs("global-log", args, 1);
                Repository.globalLog();
                break;
            case "find":
                validateNumArgs("find", args, 2);
                Repository.find(args[1]);
                break;
            case "status":
                validateNumArgs("status", args, 1);
                Repository.status();
                break;
            case "checkout":
                Repository.checkout(args);
                break;
            case "branch":
                validateNumArgs("branch", args, 2);
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                validateNumArgs("rm-branch", args, 2);
                Repository.rmBranch(args[1]);
                break;
            case "reset":
                validateNumArgs("reset", args, 2);
                Repository.reset(args[1]);
                break;
            case "merge":
                validateNumArgs("merge", args, 2);
                Repository.merge(args[1]);
                break;
            default:
                Utils.exitWithError(String.format("No command with that name exists."));
        }
    }

    /**
     * Checks the number of arguments versus the expected number,
     * throws a RuntimeException if they do not match.
     *
     * @param cmd  Name of command you are validating
     * @param args Argument array from command line
     * @param n    Number of expected arguments
     */
    private static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            Utils.exitWithError("Incorrect operands.");
        }
    }

    private static void validateDir() {
        if (!Repository.GITLET_DIR.exists()) {
            Utils.exitWithError("Not in an initialized Gitlet directory.");
        }
    }
}
