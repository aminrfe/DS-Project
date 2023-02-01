import java.util.Scanner;

public class Library {
    TrieTree persons;
    TrieTree books;

    Library() {
        persons = new TrieTree(27);
        books = new TrieTree(27);
    }

    void arrive(String personName, long time) {
        TrieTree.Node person = persons.isExist(personName);
        if(person == null){
            person = persons.add(personName);
            person.createTime();
        }
        person.isDeleted = false;
        person.arriveTime.add(time);
    }
    void exit(String personName, long time) {
        TrieTree.Node person = persons.search(personName);
        if(person == null){
            return;
        }

        person.exitTime.add(time);
        person.totalTime.add(time - person.arriveTime.peek());        
        
        if (person.totalTime.size > 1) {
            person.totalTime.set(person.totalTime.size-1, person.totalTime.peek() + person.totalTime.get(person.totalTime.size-2));
        }  
        person.isDeleted = true;
    }

    boolean isInLib(String personName) {
       if(persons.search(personName) != null)
           return true;
        return false;
    }

    void borrowBook(String personName, String bookName) { 
        TrieTree.Node book = books.search(bookName);
        TrieTree.Node person = persons.isExist(personName);
        if (book == null || book.numOfBooks == 0 || person == null){
            return;
        }
        persons.add(personName, bookName); // add book to person
        person.children[26].count2++;
        books.add(bookName, personName);   // add person to book
        book.children[26].count2++;
        book.numOfBooks--;

    }

    void returnBook(String personName, String bookName) { 
        TrieTree.Node book = books.search(bookName);
        TrieTree.Node person = persons.isExist(personName);
        if (book == null || book.children[26] == null ||book.children[26].count2 == 0|| person == null || person.children[26] == null || person.children[26].count2 == 0){
            return;
        }
        persons.search(personName+"{"+bookName).isDeleted = true;
        person.children[26].count2--;
        books.search(bookName+"{"+personName).isDeleted = true;
        book.children[26].count2--;
        book.numOfBooks++;
    }


    long TotalTimeInLib(String personName, long startTime, long endTime) {
        TrieTree.Node person = persons.isExist(personName);
        if(person == null){
            return 0;
        }
        if (endTime < person.arriveTime.get(0))
            return 0;
        long totalTime = 0;
        int i = binarySearch(person.arriveTime, 0, person.arriveTime.size-1, startTime);
        int j = binarySearch(person.exitTime, 0, person.exitTime.size-1, endTime);

        if (i > 0 && person.arriveTime.get(i) > startTime)
            i--;
        if (j > 0 && person.exitTime.get(j) > endTime)
            j--;
        if (i < person.exitTime.size-1 && startTime > person.exitTime.get(i))
            i++;
        if (j < person.exitTime.size-1 && j < person.arriveTime.size-1 && endTime > person.arriveTime.get(j+1))
            j++;
        if (i == person.arriveTime.size-1 && startTime > person.exitTime.peek())
            return 0;
        totalTime = person.totalTime.get(j)
        - (i == 0 ? 0 : person.totalTime.get(i - 1))
        - (person.arriveTime.get(i) < startTime ? startTime - person.arriveTime.get(i) : 0)
        - (person.exitTime.get(j) > endTime ? person.exitTime.get(j) - endTime : 0);
      
        return totalTime;
    }
    void addNewBook(String bookName, int count) {
        TrieTree.Node book = books.add(bookName);
       book.numOfBooks += count;
        
    }
    
    void shouldBring(String bookName, String personName) {
        TrieTree.Node book = books.search(bookName);
        if(book == null) {
            return;
        }
        if(book.numOfBooks == 0) {
            return;
        }
        if (!isInLib(personName))
            return;
        borrowBook(personName, bookName);

    }

    void allPersonCurrentBooks(String personName) {
        TrieTree.Node person = persons.isExist(personName);
        if(person == null){
            System.out.println("empty");
            return;
        }
        if(person.children[26] == null){
            System.out.println("empty");
            return;
        }
        if(person.children[26].count2 == 0){
            System.out.println("empty");
            return;
        }
        StringBuilder sb = new StringBuilder();
        persons.root.printAll(person.children[26], sb,0);
        System.out.println();
    }

    void allPersonHaveThisBook(String bookName) {
        TrieTree.Node book = books.search(bookName);
        if(book == null){
            System.out.println("empty");
            return;
        }
        if(book.children[26] == null){
            System.out.println("empty");
            return;
        }
        if(book.children[26].count2 == 0){
            System.out.println("empty");
            return;
        }
        StringBuilder sb = new StringBuilder();
        books.root.printAll(book.children[26], sb,0);
        System.out.println();
    }

    

    int binarySearch(ArrayList arr, int l, int r, long x) { 
        int mid  = 0;
        while (l <= r) { 
            mid = (l + r) / 2; 
            if (arr.get(mid) == x) 
                return mid; 
            if (arr.get(mid) < x) 
                l = mid + 1; 
            else
                r = mid - 1; 
        }
        return mid;
    }
    public static void main(String[] args) {
        Library lib = new Library();
        Scanner sc = new Scanner(System.in);
        
         
        while (sc.hasNext()) {
            String command = sc.nextLine();
            String[] parts = command.split(" ");
            if (parts[0].equals("arrive")) {
                lib.arrive(parts[1], Long.parseLong(parts[2]));
            } 
            else if (parts[0].equals("exit")) {
                lib.exit(parts[1], Long.parseLong(parts[2]));
            } 
            else if (parts[0].equals("isInLib")) {
                System.out.println(lib.isInLib(parts[1])?"YES":"NO");
            } 
            else if (parts[0].equals("borrowBook")) {
                lib.borrowBook(parts[1], parts[2]);
            } 
            else if (parts[0].equals("returnBook")) {
                lib.returnBook(parts[1], parts[2]);
            } 
            else if (parts[0].equals("totalTimeInLib")) {
                System.out.println(lib.TotalTimeInLib(parts[1], Long.parseLong(parts[2]), Long.parseLong(parts[3])));
            } 
            else if (parts[0].equals("addNewBook")) {
                lib.addNewBook(parts[1], Integer.parseInt(parts[2]));
            } 
            else if (parts[0].equals("shouldBring")) {
                lib.shouldBring(parts[1], parts[2]);
            } 
            else if (parts[0].equals("allPersonCurrentBook")) {
                lib.allPersonCurrentBooks(parts[1]);
            } 
            else if (parts[0].equals("allPersonHave")) {
                lib.allPersonHaveThisBook(parts[1]);
            } 
        }
        
        
    }
}

class TrieTree {
    int size;
    Node root;
    
    TrieTree(int size) {
        this.size = size;
        root = new Node();
    }
    
    Node add(String name) { // for add person to book and book to person
        Node current = root;
        name = name.toLowerCase();

        for(int i = 0; i < name.length(); i++){
            int index = name.charAt(i) - 'a';
            if(current.children[index] == null){
                current.children[index] = new Node();
            }
            current = current.children[index];
        }
        current.isEndOfWord = true;
        current.isDeleted = false;
        return current;
    }
    void add(String name, String book) {   // for add book to person and person to book
        add(name + "{" + book);
    }

    Node search(String name){ 
        Node current = root;
        name = name.toLowerCase();

        for(int i = 0; i < name.length(); i++){
            int index = name.charAt(i) - 'a';
            if(current.children[index] == null){
                return null;
            }
            current = current.children[index];
        }
        if(current != null && current.isEndOfWord && !current.isDeleted){
            return current;
        }
        return null;
    }

    Node isExist(String name) {
        Node current = root;
        name = name.toLowerCase();
        for(int i = 0; i < name.length(); i++){
            int index = name.charAt(i) - 'a';
            if(current.children[index] == null){
                return null;
            }
            current = current.children[index];
        }
        if(current != null && current.isEndOfWord){
            return current;
        }
        return null;
    }
    
    void remove(String name){
        Node current = root;
        name = name.toLowerCase();

        for(int i = 0; i < name.length(); i++){
            int index = name.charAt(i) - 'a';
            if(current.children[index] == null){
                return;
            }
            current = current.children[index];
        }
        if(current != null && current.isEndOfWord && !current.isDeleted){
            current.isDeleted = true;
        }    
    }

    void remove(String name, String book){ // for remove book from person and person from book
        remove(name + "{" + book);
    }
    
    class Node {
        Node[] children = new Node[size];
        boolean isEndOfWord;
        boolean isDeleted;
        int numOfBooks;
        int count;          
        ArrayList arriveTime;
        ArrayList exitTime;
        ArrayList totalTime;
        int count2;
        


        Node() {
            for (int i = 0; i < size; i++)
                children[i] = null;  
            isEndOfWord = false;
            isDeleted = true;
        }
        void createTime() {
            arriveTime = new ArrayList();
            exitTime = new ArrayList();
            totalTime = new ArrayList();
        }
        
        void printAll(Node root, StringBuilder sb, int index) {
            if (root == null)
                return;
            if (root.isEndOfWord && !root.isDeleted) {
                System.out.print(sb + " ");
            }
            for (int i = 0; i < size; i++) {
                if (root.children[i] != null) {
                    sb.append((char) (i + 'a'));
                    printAll(root.children[i], sb, index);
                    sb.deleteCharAt(sb.length() - 1);
                }
            }
        }
    

    }
    
}
class ArrayList {
    long[] arr;
    int size;
    int capacity;
    ArrayList() {
        arr = new long[10];
        size = 0;
        capacity = 10;
    }
    void add(long x) {
        if (size == capacity) {
            long[] temp = new long[capacity * 2];
            for (int i = 0; i < size; i++) {
                temp[i] = arr[i];
            }
            arr = temp;
            capacity *= 2;
        }
        arr[size++] = x;
    }   
    long get(int index) {
        return arr[index];
    }
    void set(int index, long x) {
        arr[index] = x;
    }
    void remove(int index) {
        for (int i = index; i < size - 1; i++) {
            arr[i] = arr[i + 1];
        }
        size--;
    }
    int size() {
        return size;
    }
    long peek() {
        return arr[size - 1];
    }
    int indexPeek() {
        return size - 1;
    }
}