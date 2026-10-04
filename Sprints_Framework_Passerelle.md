# Feuille de Route des Sprints - Framework Passerelle

## Sprint 0 : Interception des requêtes (Front Controller)
* **Principe :** Mettre en place le point d'entrée unique de l'application. On configure un Servlet central (`FrontControllerServlet`) mappé sur `/*` dans le `web.xml`. Son rôle est d'intercepter toutes les requêtes HTTP entrantes et d'extraire l'URL appelée (`getRequestURI()`).

---

## Sprint 1 : Scan et détection des contrôleurs
* **Principe :** Identifier les classes qui jouent le rôle de contrôleur. On crée l'annotation de classe `@Controller`. Au démarrage de l'application (dans la méthode `init()`), le framework scanne le dossier du package spécifié (`packageToScan`), charge les classes via le `ClassLoader` et filtre celles qui possèdent l'annotation `@Controller`.

---

## Sprint 2 : Cartographie des URL et méthodes (Mapping)
* **Principe :** Associer chaque URL à une méthode précise d'un contrôleur. On crée une annotation de méthode (ex: `@GetMapping` ou `@Url`) et une classe de structure `Mapping` (contenant le nom de la classe et le nom de la méthode). Lors du scan au démarrage, le framework parcourt les méthodes des contrôleurs et remplit un dictionnaire central (`HashMap<String, Mapping>`) où la clé est l'URL et la valeur est le `Mapping` correspondant.

---

## Sprint 3 : Exécution dynamique de la méthode (Invocation)
* **Principe :** Exécuter la logique métier correspondant à l'URL demandée. Quand une requête arrive, le `FrontController` cherche l'URL dans la `HashMap`. S'il la trouve, il instancie dynamiquement la classe du contrôleur et invoque la méthode associée en utilisant la Réflexion Java (`method.invoke()`).

---

## Sprint 4 : Injection automatique des paramètres
* **Principe :** Passer les données envoyées par le client (formulaire ou paramètres d'URL) directement aux arguments de la méthode du contrôleur. Le framework inspecte les noms et types des paramètres attendus par la méthode Java, récupère les valeurs correspondantes depuis `request.getParameter()`, effectue le transtypage (conversion de `String` vers `int`, `double`, etc.) et les injecte lors de l'appel de la méthode.

---

## Sprint 5 : Restitution MVC et redirection vers la vue (`ModelView`)
* **Principe :** Séparer la logique métier de l'affichage (principe MVC). Au lieu de faire des `out.println()` dans le contrôleur, la méthode retourne un objet dédié (`ModelView`) contenant le chemin de la vue (ex: `liste-employes.jsp`) et les données à transmettre. Le `FrontController` récupère cet objet, injecte les données dans la requête (`request.setAttribute()`) et effectue une redirection côté serveur (`RequestDispatcher.forward()`) vers la page JSP.

---

## Sprint 6 : Refactorisation avec `FrameListener` (`ServletContextListener`)
* **Principe :** Séparer les responsabilités au démarrage. On retire la logique lourde de scan et de cartographie du `init()` du `FrontControllerServlet` pour la placer dans un Listener d'application (`FrameListener` implémentant `ServletContextListener`). Au lancement du serveur, le `FrameListener` effectue le scan, construit le dictionnaire de `Mapping` et le stocke dans le `ServletContext`. Le `FrontControllerServlet` devient ainsi un simple contrôleur d'exécution au runtime.

---

## Sprint 7 : Configuration et gestion de la connexion à la base de données
* **Principe :** Centraliser et fournir une gestion de la base de données au sein du framework. Les identifiants et paramètres de connexion (`db.driver`, `db.url`, `db.user`, `db.password`) sont déclarés dans le `web.xml` via des `<context-param>`. Le framework les charge au démarrage (via le `FrameListener`) et fournit un utilitaire ou une classe de gestion permettant aux contrôleurs ou services de récupérer une connexion JDBC active pour exécuter leurs requêtes BDD.

## Sprint 8:

## sprint 7:
* **Principe :** mahafantatra ny lalana ny paramètres par paramètres d'un formulaire;

## sprint 7 -b:
* **Principe:** objet directement pour 