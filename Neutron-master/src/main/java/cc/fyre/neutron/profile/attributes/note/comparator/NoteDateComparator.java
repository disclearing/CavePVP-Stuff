package cc.fyre.neutron.profile.attributes.note.comparator;

import cc.fyre.neutron.profile.attributes.note.Note;

import java.util.Comparator;

public class NoteDateComparator implements Comparator<Note> {

    @Override
    public int compare(Note note,Note otherNote) {
        return Long.compare(note.getExecutedAt(),otherNote.getExecutedAt());
    }
}
