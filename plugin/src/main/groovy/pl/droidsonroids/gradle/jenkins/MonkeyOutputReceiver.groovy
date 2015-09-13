package pl.droidsonroids.gradle.jenkins

import com.android.ddmlib.MultiLineReceiver

class MonkeyOutputReceiver extends MultiLineReceiver {

    PrintWriter printWriter

    MonkeyOutputReceiver(File outputFile) throws IOException {
        printWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputFile, true), 16 * 1024))
    }

    @Override
    public void processNewLines(String[] lines) {
        lines.each { printWriter.println(it) }
    }

    @Override
    public void done() {
        printWriter.close()
    }

    @Override
    public boolean isCancelled() {
        false
    }
}