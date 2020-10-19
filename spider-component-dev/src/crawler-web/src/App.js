
import './common/styles/index.scss';
import React, { Component } from 'react';
import RouterComponent from './route/index';

class App extends Component {
  render() {
    return (
      <div className="App">
        <RouterComponent />
      </div>
    );
  }
}

export default App;