# Annie's Pizza Questions

- Should the customer be notified about the allergens or filter them out (from the whole list or the shopping cart)?
    - Users can filter the whole menu list, not their shopping cart. They can only filter based on their own allergies in their profile (for simplicity we only assume they order for themselves, so only take their own allergies into account, not all the allergies). If users add a pizza to their order that has allergies, they receive a flag about it (how you implement this is up to you).

- How do notifications work exactly?
    - Notification is done by sending an email to the store owner that relates to that store. No Web Sockets allowed for this project! Use REST APIs.

- Are the store and regional manager different things?
    - Yes. The software is only for a pizza chain in Delft. So there is one regional manager that supervises this chain.

- Is there only one regional manager?
    - There's one regional manager for all the stores, and each store has its store owner.

- How does the buy-1 get-1-free coupon work exactly? Let's say we have several pizzas in one order and apply this coupon, does the customer get the cheapest pizza of all in the selection for free or the most expensive one? And what happens if there is only one pizza in the order?
    - You can apply it anytime you want, but it depends on the order if it comes into effect. If your order only has one pizza and you apply it, it does nothing. If you have multiple pizzas and you apply it, it makes the cheapest pizza free. 

- Should the user be able to cancel the coupon he already entered?
    - Yes. If a user cancels their order, they also get their coupon back.

- Should the user be able to make multiple orders at the same time?
    - Yes.

- If the user aborts the payment, does the store owner also receive an order notification + a cancellation or he doesn't know anything at all, should the order state be recovered or cleared?
    - No. There is not payment system, so the user just says "Order" at the end and the order is created. If he "didn't pay" (aka didn't go through with it and order at the end), then no order was placed so the no notification is sent to the storer owner 

- If the regional manager canceled the order, should the customer get notification about that? 
    - No, in the scenario it's mentioned that only store owners receive notifications.

- How does the time system work? Do we generate current time as the order time or do we use made-up variables?
    - Yes, just use real time. You can create helper functions for testing and development purposes

- Do we need to handle the case that the pick up time is invalid?
    - Yes, no times in the past, only in the reasonable future (so enough time in advance, not like 5 mins).

- How should payment work?
    - No payment system! This is out of the scope of the project. Users just order and that's it, the order is placed. We assume the users always have enough money to order and they're not malicious. Orders still have a price (so that coupons can be applied), but no payment system. Keep it simple!

- Can you filter on only your chosen allergens or on any allergen?
    - Only your chosen allergens.

- Should we support multiple types of pizza bottoms (normal dough, cauliflower, dough, etc..)?
    - No. Keep it simple, you just have pizza dough.

- Should customers without an account also be able to order pizza’s?
    - No. 

- Should stores have different menus?
    - No. One menu for all stores.

- Can regional managers modify the menu of a specific store or of all the stores in their assigned region?
    - Regional managers modify the menus of all the stores. Only the regional manager can do it, store owners don't have this privilege.

- Coupons are specific to each store.
- Store owners or the regional manager can create the coupons. If the store owner create the coupon it's only for their store, whilst if the regional manager creates it, it's for all the stores.
- There is only one regional manager for all the stores.
- Ways to do coupons: reusable coupons by the same user (e.g. Christmas coupon that you can apply for every order during Christmas period), reusable for different users (same code shared to multiple users, each user can use it once), code that can only be used once (by one user). Choose which impplementation you prefer and justify it.
- Regional managers and store owner accounts can be hardcoded.
- No Web Sockets allowed for this project! Use REST APIs.
- Seeing only the active orders is a Must. Seeing all the orders (history) is not a Must.  
- Keep allergens simple. Have some dummy ones such as allergic to ham, allergic to dough, etc., don't overcomplicate (e.g. don't say the dough contains milk, and people are allergic to milk, keep it simple).
- Stores have infinite pizzas, infinite ingredients, don't take stock into account (so stores can't be out of ingredients or pizzas).
- Toppings: a pizza has a list of default topping which are the toppings it comes hardcoded with (e.g. Hawaii pizza comes with pineapple and ham by default). Any other topping added over the default one is considered custom. All toppings on the pizza, whether they're default or custom, can be editted (remove toping/add toping).