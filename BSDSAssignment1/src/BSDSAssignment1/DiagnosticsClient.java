package BSDSAssignment1;

import javax.tools.Diagnostic;
import java.util.Locale;

/**
 * Created by songyang on 10/17/16.
 */
public class DiagnosticsClient implements Diagnostic<CAServer> {
    @Override
    public Kind getKind() {
        return null;
    }

    @Override
    public CAServer getSource() {
        return null;
    }

    @Override
    public long getPosition() {
        return 0;
    }

    @Override
    public long getStartPosition() {
        return 0;
    }

    @Override
    public long getEndPosition() {
        return 0;
    }

    @Override
    public long getLineNumber() {
        return 0;
    }

    @Override
    public long getColumnNumber() {
        return 0;
    }

    @Override
    public String getCode() {
        return null;
    }

    @Override
    public String getMessage(Locale locale) {
        return null;
    }
}
