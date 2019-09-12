package synchmanager

import org.apache.log4j.BasicConfigurator

fun main(args: Array<String>) {
    // TODO: Connect on master network manager and do the same
    BasicConfigurator.configure()
    SynchManagerRunner.start()
}