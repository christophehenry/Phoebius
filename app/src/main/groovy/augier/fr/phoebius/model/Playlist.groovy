package augier.fr.phoebius.model

import augier.fr.phoebius.providers.Song
import com.arasthel.swissknife.annotations.Parcelable

@Parcelable
public class Playlist implements List<Song>
{
    private ArrayList<Song> songs = []

    Playlist(){}

    Playlist(List<Song> songs){ this.songs.addAll(songs) }

    public Playlist shuffle()
    {
        Collections.shuffle(songs)
        return this
    }

    public Playlist sort()
    {
        songs.sort()
        return this
    }

    public Playlist clone(){ return new Playlist(songs) }

    //region OVERRIDEN
    @Override void add(int i, Song song){ songs.add(i, song) }

    @Override boolean add(Song song){ return songs.add(song) }

    @Override boolean addAll(int i, Collection<? extends Song> c){ return songs.addAll(i, c) }

    @Override boolean addAll(Collection<? extends Song> c){ return songs.addAll(c) }

    @Override void clear(){ songs.clear() }

    @Override boolean contains(Object o){ return songs.contains(o) }

    @Override boolean containsAll(Collection<?> c){ return songs.containsAll(c) }

    @Override Song get(int i){ return songs.get(i) }

    @Override int indexOf(Object o){ return songs.indexOf(o) }

    @Override boolean isEmpty(){ return songs.isEmpty() }

    @Override Iterator<Song> iterator(){ return songs.iterator() }

    @Override int lastIndexOf(Object o){ return songs.lastIndexOf(o) }

    @Override ListIterator<Song> listIterator(){ return songs.listIterator() }

    @Override ListIterator<Song> listIterator(int i){ return songs.listIterator(i) }

    @Override Song remove(int i){ return songs.remove(i) }

    @Override boolean remove(Object o){ return songs.remove(o) }

    @Override boolean removeAll(Collection<?> c){ return songs.removeAll(c) }

    @Override boolean retainAll(Collection<?> c){ return songs.retainAll(c) }

    @Override Song set(int i, Song song){ return songs.set(i, song) }

    @Override int size(){ return songs.size() }

    @Override Playlist subList(int i, int i1){ return new Playlist(songs.subList(i, i1)) }

    @Override Object[] toArray(){ return songs.toArray() }

    @Override def <T> T[] toArray(T[] ts){ return songs.<T> toArray(ts) }

    //endregion
}
