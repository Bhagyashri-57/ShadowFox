import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class ContactManagement {
    private String name;
    private long phone;
    private String email;
    List<ContactManagement>contacts=new ArrayList<>();

    static Scanner sc = new Scanner(System.in);
    
    public void setName(String name){
        this.name=name;
    }
    public String getName(){
        return this.name;
    }
    public void setPhone(long phone){
        this.phone=phone;
    }
    public long getPhone(){
        return this.phone;
    }
    public void setEmail(String email){
        this.email=email;
    }
    public String getEmail(){
        return this.email;
    }

    public ContactManagement(){
    }
        public ContactManagement(String name,long phone,String email){
            this.name=name;
            this.phone=phone;
            this.email=email;

        }
        public void addContact(){
            System.out.println("Enter the name: " );
            String name=sc.next();
            System.out.println("Enter the number: " );
            long number = sc.nextLong();
            System.out.println("Enter the Email: " );
            String email= sc.next();

            ContactManagement contact = new ContactManagement(name,phone,email);
            contacts.add(contact);
            System.out.println("Contact " + number + " Added Successfully!");
        }
        public void veiwContacts(){
            if(contacts.isEmpty()){
                System.out.println("Contacts is empty! ");
                return;
            }
            System.out.println("Total Contacts: ");
            int i=1;
            for(ContactManagement c : contacts ){
                System.out.println("Contact #" + i);
                System.out.println("Name : "+c.name);
                System.out.println("Phone : "+c.phone);
                System.out.println("Email : "+c.email);
                System.out.println("\n");
                i++;
            }
        }

        public int isValidContact(long number){

            int index = 0;
            for(ContactManagement c: contacts){
                if(number==c.phone){
                    return index;
                }
                index++;
            }
            return -1;
        }
        public void updateNumber(int index){
            System.out.println("Enter the new number to update: ");
            long newNumber=sc.nextLong();
            contacts.get(index).phone=newNumber;
            System.out.println("Update New Numbers : "+newNumber);
        }
        public void updateName(int index){
            System.out.println("Enter the new name to update : ");
            String newName=sc.next();
            contacts.get(index).name=newName;
            System.out.println("Update New Name : "+newName);
        }
        public void updateEmail(int index){
            System.out.println("Enter the new Email to update: ");
            String newEmail=sc.next();
            contacts.get(index).email=email;
            System.out.println("Update New Email : "+newEmail);

        }
        public void updateContacts(){
            if(contacts.isEmpty()){
                System.out.println("Contacts is empty!");
                return;
            }
            System.out.println("Enter the number to update: ");
            long number = sc.nextLong();
            int targetindex=isValidContact(number);
            if(targetindex!=-1){
                System.out.println("Choose from the below : ");
                System.out.println("1. Update number ");
                System.out.println("2. Update name ");
                System.out.println("3. Udate Email: ");
                System.out.println("\n");
                int option = sc.nextInt();
                switch(option){
                    case 1:
                        updateNumber(targetindex);
                        break;

                    case 2:
                        updateName(targetindex);
                        break;
                    
                    case 3:
                        updateEmail(targetindex);
                        break;

                    default:
                        System.out.println("Invalid Option! ");
                        break;
                }

            }
            else{
                System.out.println("Invalid number! "+number);
            }
        }
        public void deleteContact(){
            if(contacts.isEmpty()){
            System.out.println("Contacts is empty!");
            return;
        }
        System.out.println("Enter the number to delete: ");
        long number = sc.nextLong();
        int targetIndex=isValidContact(number);
        if(targetIndex!=-1){
            contacts.remove(targetIndex);
            System.out.println("Contact " + number + "Deleted Successfully ");

        }
        else{
            System.out.println("Invalid number!"+number);
        }
    }
    public static void main(String[]args){
        ContactManagement cm = new ContactManagement();
        System.out.println("==== Welcome To Contact Manager ==== ");
        while (true) {
        System.out.println("Select from the below Options: " );
        System.out.println("1.ADD CONTACT");
        System.out.println("2.VIEW CONTACT");
        System.out.println("3.UPDATE CONTACT");
        System.out.println("4.DELETE CONTACT");
        System.out.println("5.EXIT");
        int option =sc.nextInt();
        switch(option){
            case 1:
                cm.addContact();
                break;

            case 2:
                cm.veiwContacts();
                break;
            
            case 3:
                cm.updateContacts();
                break;

            case 4:
                cm.deleteContact();
                break;
            
            case 5:
                System.out.println("Thanks,visit Again!");
                System.exit(0);
                break;

            default:
                System.out.println("Invalid option! Please try again!");
                break;
        }
        }
            
        }
    }

    
    

    

