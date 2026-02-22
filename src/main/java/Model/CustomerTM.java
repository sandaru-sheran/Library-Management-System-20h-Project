package Model;

    public class CustomerTM {
        private String custId;
        private String name;
        private String address;
        private String contact;
        private String email;

        public CustomerTM() {
        }

        public CustomerTM(String custId, String name, String address, String contact, String email) {
            this.custId = custId;
            this.name = name;
            this.address = address;
            this.contact = contact;
            this.email = email;
        }

        // Getters and Setters are REQUIRED for JavaFX PropertyValueFactory

        public String getCustId() {
            return custId;
        }

        public void setCustId(String custId) {
            this.custId = custId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
