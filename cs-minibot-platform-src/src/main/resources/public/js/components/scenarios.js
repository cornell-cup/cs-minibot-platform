var React = require('react');

/**
 * Component for the new scenarios system
 * Will contain:
 * Loading, saving, editing, adding scenarios to simulator
 *
 */
export default class Scenarios extends React.Component {
    //TODO
    render() {
        return(
            <div id ="component_scenarios" className = "box">Scenarios</div>
        )
    }
}
//
// //adding a scenario from the value in the scenario viewer
// $('#addScenario').click(function() {
//     console.log("add scenario from interface.js")
//     var scenario = $("#scenario").val();
//
//     $.ajax({
//         method: "POST",
//         url: '/addScenario',
//         dataType: 'text',
//         data: JSON.stringify({
//             scenario: scenario.toString()
//         }),
//         contentType: 'application/json; charset=utf-8',
//         success: function (data){
//             console.log("successfully added scenario: "+data);
//         }
//     });
// });
//
// //saving a scenario to a txt file with the specified filename
// $('#saveScenario').click(function() {
//     console.log("saving a scenario")
//     var scenario = $("#scenario").val();
//     var filename = $("#scenarioname").val();
//
//     $.ajax({
//         method: "POST",
//         url: '/saveScenario',
//         dataType: 'text',
//         data: JSON.stringify({scenario: scenario.toString(),
//             name: filename.toString()}),
//         contentType: 'application/json; charset=utf-8',
//         success: function (data){
//             console.log("successfully saved scenario: "+data);
//         }
//     });
// });
//
// /**loading a scenario - just type in a name, no need for directory or file
//  extension*/
// $('#loadScenario').click(function() {
//     active_bots = [];
//     discovered_bots = [];
//
//     var filename = $("#scenarioname").val();
//     console.log("loading scenario: "+filename.toString());
//     $.ajax({
//         method: "POST",
//         url: '/loadScenario',
//         dataType: 'text',
//         data: JSON.stringify({'name':filename.toString()}),
//         contentType: 'application/json; charset=utf-8',
//         success: function (data){
//             $("#scenario").val(data);
//             console.log("successfully loaded scenario: "+data);
//         },
//         error: function(data){
//             console.log("error: please enter the name of an existing scenario")
//         }
//     });
// });