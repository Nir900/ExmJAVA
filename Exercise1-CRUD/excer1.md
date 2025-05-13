<div dir="rtl">

# תרגיל Spring Boot - פעולות CRUD מול MySQL

תרגיל זה יסביר כיצד ליצור אפליקציית Spring Boot בסיסית המבצעת פעולות CRUD מול בסיס נתונים MySQL.

## דרישות מקדימות

- JDK 17 או גרסה מתקדמת יותר
- JAVA
- Maven 
- MySQL Server

## שלב 1: יצירת פרויקט Spring Boot

- השתמש ב-[Spring Initializr](https://start.spring.io/) ליצירת פרויקט בסיסי
- הוסף את התלויות הבאות:
    - Spring Web
    - Spring Data JPA
    - MySQL Driver
    - Lombok (אופציונלי)

## שלב 2: הגדרת בסיס הנתונים

- צור בסיס נתונים חדש ב-MySQL בשם `springbootdb`
- הגדר את פרטי החיבור בקובץ `application.properties`:

</div>

    - `spring.datasource.url` - כתובת החיבור לבסיס הנתונים
    - `spring.datasource.username` - שם המשתמש
    - `spring.datasource.password` - סיסמה
    - `spring.jpa.hibernate.ddl-auto=update` - יצירה/עדכון אוטומטי של טבלאות
    - `spring.jpa.show-sql=true` - הצגת שאילתות SQL בלוג

<div dir="rtl">

## שלב 3: יצירת מודל

צור מחלקת מודל של מוצר (`Product`) עם האנוטציות הנדרשות:

</div>

- `@Entity` - הגדר את המחלקה כישות
- `@Table(name = "products")` - הגדר את שם הטבלה בבסיס הנתונים
- `@Id` - סמן את המזהה הייחודי
- `@GeneratedValue` - הגדר יצירה אוטומטית של מזהים
- `@Column` - הגדר מאפיינים של העמודות בטבלה

<div dir="rtl">

השדות הנדרשים:

</div>

- `id` (Long) - מזהה ייחודי
- `name` (String) - שם המוצר
- `description` (String) - תיאור המוצר
- `price` (Double) - מחיר המוצר
- `quantity` (Integer) - כמות במלאי

<div dir="rtl">

## שלב 4: יצירת Repository

צור ממשק `ProductRepository` שיורש מ- `<JpaRepository<Product, Long`:

- השתמש באנוטציה `Repository@`
- Spring Data JPA יספק את המימוש האוטומטי לפעולות CRUD הבסיסיות:

</div>

    - `save()` - יצירה ועדכון
    - `findById()` - קריאה לפי מזהה
    - `findAll()` - קריאת כל הרשומות
    - `delete()` - מחיקת רשומה

<div dir="rtl">

- הוסף מתודות חיפוש מותאמות אישית:
    - `findByNameContaining(String name)` - חיפוש לפי חלק משם המוצר
    - `findByPriceLessThan(Double price)` - חיפוש מוצרים מתחת למחיר מסוים

<div dir="rtl">

## שלב 5: יצירת Service

צור מחלקת שירות `ProductService` שתכיל את הלוגיקה העסקית:

</div>

- השתמש באנוטציה `Service@`
- הזרק את ה-`ProductRepository` באמצעות `Autowired@`
- יישם את הפעולות הבאות:
    - `createProduct(Product product)` - יצירת מוצר חדש
    - `getAllProducts()` - קבלת כל המוצרים
    - `getProductById(Long id)` - קבלת מוצר לפי מזהה
    - `updateProduct(Long id, Product productDetails)` - עדכון מוצר קיים
    - `deleteProduct(Long id)` - מחיקת מוצר

<div dir="rtl">

## שלב 6: יצירת Controller

צור מחלקת בקר `ProductController` שתחשוף את ה-API:



- השתמש באנוטציה `RestController@`
- הגדר את נתיב הבסיס באמצעות `RequestMapping("/api/products")@`
- הזרק את ה-`ProductService` באמצעות `Autowired@`
- יישם את הפעולות הבאות:

</div>

    - `createProduct(@RequestBody Product product)` - `@PostMapping`
    - `getAllProducts()` - `@GetMapping`
    - `getProductById(@PathVariable Long id)` - `@GetMapping("/{id}")`
    - `updateProduct(@PathVariable Long id, @RequestBody Product product)` - `@PutMapping("/{id}")`
    - `deleteProduct(@PathVariable Long id)` - `@DeleteMapping("/{id}")`

- החזר תגובות מתאימות עם קודי סטטוס HTTP (`ResponseEntity`)

<div dir="rtl">

## שלב 7: בדיקת קיום מחלקת Application ראשית

 מחלקה ראשית `CrudDemoApplication` עם:

- אנוטציה `SpringBootApplication@`
- מתודת `main` שמפעילה את האפליקציה
- 
## שלב 8: בדיקת האפליקציה

הפעל את האפליקציה והשתמש באחת מהשיטות הבאות לבדיקת ה-API:

### אפשרות 1: בדיקה באמצעות IntelliJ Ultimate

אם יש ברשותך IntelliJ Ultimate, תוכל להשתמש בכלי HTTP Client המובנה:



1. צור קובץ חדש עם סיומת `.http` (לדוגמה: `product-api.http`)
2. הוסף את הבקשות הבאות לקובץ והשתמש בכפתור ה-"Run" לצד כל בקשה:

</div>

<div dir="ltr">

```http
### Create new product
POST http://localhost:8080/api/products
Content-Type: application/json

{
    "name": "Laptop",
    "description": "Powerful development laptop",
    "price": 4500.0,
    "quantity": 10
}

### Get all products
GET http://localhost:8080/api/products

### Get product by ID
GET http://localhost:8080/api/products/1

### Update product
PUT http://localhost:8080/api/products/1
Content-Type: application/json

{
    "name": "Upgraded Laptop",
    "description": "Powerful development laptop with enhanced memory",
    "price": 5000.0,
    "quantity": 8
}

### Delete product
DELETE http://localhost:8080/api/products/1
```

</div>

<div dir="rtl">

### אפשרות 2: בדיקה באמצעות Postman

1. התקן את [Postman](https://www.postman.com/downloads/)
2. צור בקשות חדשות עבור כל פעולה:


</div>

<div dir="ltr">

#### יצירת מוצר חדש (Create)

- Method: `POST`
- URL: `http://localhost:8080/api/products`
- Headers: `Content-Type: application/json`
- Body (raw, JSON):
```json
{
    "name": "Laptop",
    "description": "Powerful development laptop",
    "price": 4500.0,
    "quantity": 10
}
```

#### קבלת כל המוצרים (Read All)
- Method: `GET`
- URL: `http://localhost:8080/api/products`

#### קבלת מוצר לפי מזהה (Read One)
- Method: `GET`
- URL: `http://localhost:8080/api/products/1`

#### עדכון מוצר (Update)
- Method: `PUT`
- URL: `http://localhost:8080/api/products/1`
- Headers: `Content-Type: application/json`
- Body (raw, JSON):
```json
{
    "name": "Upgraded Laptop",
    "description": "Powerful development laptop with enhanced memory",
    "price": 5000.0,
    "quantity": 8
}
```

#### מחיקת מוצר (Delete)
- Method: `DELETE`
- URL: `http://localhost:8080/api/products/1`


</div>

<div dir="rtl">

שימו לב להוסיף הערות באנגלית לאורך הקוד להסבר הפונקציונליות השונה.

</div>