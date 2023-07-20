# pjatk-prm-debtor-app

This project contains an Android project developed in accordance to the requirements from PRM subject at PJATK

## Requirements

- The content of the project enriched with demonstrative assessment criteria. Please note that the final grade belongs to the evaluator.
- In the assessment of the project, apart from practical and substantive correctness, the quality and readability of the code written by you will also be assessed. [2p]
- The project should be implemented using native Android SDK tools in kotlin language variant.

### Task

Create an application to manage your friends' debts.
The application will contain 3 screens:

1. Debt list screen:

- The list of debtors will be displayed on the screen. For each debtor will be visible information with the name of the person and the debt incurred. [1p]
- The list of debtors should be saved in the local file system in the form of a local database (sqlite or similar) [3p]
- Choosing a list item will allow you to edit it, and holding it down longer will cause an alert with a request for debt cancellation. If the user approves the cancellation - the debtor disappears from the list along with the debt. [2p]
- The screen should also contain a button to add a new debtor and the sum of all debts. [1p]

2. Debtor adding/editing screen - starts after clicking the add debtor button or when editing an existing debtor on the list.

- This screen allows you to give/change the name of the debtor and his debt.
- Button for saving changes or undoing changes (in the case of attempting to undo changes made to an existing entry, a confirmation alert should also be displayed). [2p]
- This screen should also contain a button enabling simulation of debt repayment and a button to notify the debtor about the debt. (providing textual information in any application that allows text sharing, for example, SMS) [2p]

3. Simulation screen - starts when you choose a debt repayment simulation. The user can set the debt repayment speed (amount of dolars per second) and the interest rate percentage.

- The screen contains information about the debtor and its debt in text form and a button to run the simulation.
- Running the simulation causes the reduction of the debt value every second by the value specified in the field "debt repayment speed" and then adds the interest rate calculated on its basis to the remaining amount of debt. [1p]
- The simulation can be stopped at any time or when the debt is fully paid off. Then, additional information is displayed on how much interest the debtor had to pay in total to pay off the debt with the given parameters. [1p]

The application should have some graphic design (e.g. icons instead of text on buttons and logo on the list screen) [2p]
The application should implement ContentProvider providing data about debtors for other applications that will be interested in this data. [3p]
