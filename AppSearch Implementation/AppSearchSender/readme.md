How AppSearch Works (The Architecture)

Instead of sending an Intent and hoping for a result, the "Target App" indexes its data into a central system service. The "Sender App" then queries that system service.
Key Components:

    AppSearchSession: Used by the app that owns the data to save documents.

    GlobalSearchSession: Used by other apps to search for those documents (requires permission).

    Schema: A template (like a class) that defines what a "Searchable Item" looks like.