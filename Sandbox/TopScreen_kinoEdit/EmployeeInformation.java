public class EmployeeInformation {
  //テスト用
  String id;
  String name;
  String age;
  String experience;
  String languages;

  EmployeeInformation(String id, String name, String age, String experience, String languages) {
      this.id = id;
      this.name = name;
      this.age = age;
      this.experience = experience;
      this.languages = languages;
  }

  String[] toArray() {
      return new String[]{id, name, age, experience, languages};
  }
}

